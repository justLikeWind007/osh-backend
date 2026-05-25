package com.backstage.framework.consumer;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.constant.KafkaConstants;
import com.backstage.system.domain.order.enums.ProductTypeEnum;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.domain.vo.pay.OrderCheckoutReqVO;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import com.backstage.system.service.order.OrderCheckoutService;
import com.backstage.system.service.order.OrderService;
import com.backstage.system.service.impl.seckill.OshSeckillOrderServiceImpl.SeckillOrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀订单创建 Kafka 消费者
 * 消费 seckill.order.create Topic，完成：
 *   1. 调用 OrderCheckoutService.checkout() 写 osh_order + 调支付平台
 *   2. 扣减数据库库存
 *   3. 写入 osh_seckill_order
 *   4. 更新 Redis orderKey 为真实 orderNo
 * 任意步骤失败时按已完成的步骤逐层回滚。
 */
@Component
public class SeckillOrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderConsumer.class);

    @Autowired
    private OshSeckillOrderMapper orderMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private OrderCheckoutService orderCheckoutService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @KafkaListener(
            topics = KafkaConstants.SECKILL_ORDER_CREATE_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeSeckillOrder(String message, Acknowledgment ack) {
        logger.info("【秒杀消费者】收到订单创建消息：{}", message);

        SeckillOrderMessage msg;
        try {
            msg = JSON.parseObject(message, SeckillOrderMessage.class);
        } catch (Exception e) {
            logger.error("【秒杀消费者】消息解析失败，直接丢弃，message={}", message, e);
            ack.acknowledge();
            return;
        }

        // 幂等校验：orderKey 已经是真实 orderNo（非 PENDING）说明已处理过
        Object existingVal = redisTemplate.opsForValue().get(msg.getOrderKey());
        if (existingVal != null && !"PENDING".equals(existingVal.toString())) {
            logger.warn("【秒杀消费者】订单已处理，跳过重复消费，orderKey={}, val={}", msg.getOrderKey(), existingVal);
            ack.acknowledge();
            return;
        }

        // 也检查数据库，防止 Redis Key 过期后重复消费
        // 用 activityId + itemId + userId 查是否已有订单
        OshSeckillOrder existOrder = orderMapper.selectOrderByActivityAndUser(
                msg.getActivityId(), msg.getItemId(), msg.getUserId());
        if (existOrder != null) {
            logger.warn("【秒杀消费者】数据库订单已存在，跳过重复消费，activityId={}, itemId={}, userId={}",
                    msg.getActivityId(), msg.getItemId(), msg.getUserId());
            ack.acknowledge();
            return;
        }

        int quantity = msg.getQuantity() != null ? msg.getQuantity() : 1;
        String orderNo = null;

        // 步骤1：调用统一支付模块创建订单并发起支付
        try {
            OrderCheckoutReqVO checkoutReq = new OrderCheckoutReqVO();
            checkoutReq.setUserId(msg.getUserId());
            checkoutReq.setProductType(ProductTypeEnum.SECKILL.getCode());
            checkoutReq.setProductId(msg.getGoodsId());
            checkoutReq.setProductName(msg.getGoodsTitle());
            checkoutReq.setActivityId(msg.getActivityId());
            checkoutReq.setOriginalAmount(msg.getOriginPrice());
            checkoutReq.setPayableAmount(msg.getSeckillPrice().multiply(BigDecimal.valueOf(quantity)));
            checkoutReq.setClientIp(msg.getClientIp() != null ? msg.getClientIp() : "seckill-consumer");

            OrderCheckoutRespVO checkoutResp = orderCheckoutService.checkout(checkoutReq);
            orderNo = checkoutResp.getOrderNo();
            logger.info("【秒杀消费者】统一订单创建成功，orderNo={}", orderNo);
        } catch (Exception e) {
            // checkout 失败：回滚 Redis 库存和已购数量，删除 orderKey
            logger.error("【秒杀消费者】checkout 失败，回滚 Redis，activityId={}, itemId={}, userId={}",
                    msg.getActivityId(), msg.getItemId(), msg.getUserId(), e);
            rollbackRedis(msg, quantity);
            ack.acknowledge();
            return;
        }

        // 步骤2：扣减数据库库存
        try {
            int affected = itemMapper.decrStock(msg.getItemId(), quantity);
            if (affected == 0) {
                logger.error("【秒杀消费者】数据库库存不足，回滚，itemId={}, orderNo={}", msg.getItemId(), orderNo);
                cancelOrderAndRollbackRedis(orderNo, msg, quantity);
                ack.acknowledge();
                return;
            }
            logger.info("【秒杀消费者】数据库库存扣减成功，itemId={}, quantity={}", msg.getItemId(), quantity);
        } catch (Exception e) {
            logger.error("【秒杀消费者】扣减数据库库存异常，回滚，itemId={}, orderNo={}", msg.getItemId(), orderNo, e);
            cancelOrderAndRollbackRedis(orderNo, msg, quantity);
            ack.acknowledge();
            return;
        }

        // 步骤3：写入秒杀订单表
        try {
            OshSeckillOrder order = new OshSeckillOrder();
            order.setSeckillNo(orderNo);
            order.setActivityId(msg.getActivityId());
            order.setItemId(msg.getItemId());
            order.setUserId(msg.getUserId());
            order.setGoodsId(msg.getGoodsId());
            order.setGoodsType(msg.getGoodsType());
            order.setGoodsTitle(msg.getGoodsTitle());
            order.setGoodsCover(msg.getGoodsCover());
            order.setOriginPrice(msg.getOriginPrice());
            order.setSeckillPrice(msg.getSeckillPrice());
            order.setTotalAmount(msg.getSeckillPrice().multiply(BigDecimal.valueOf(quantity)));
            order.setQuantity(quantity);
            order.setStatus(0); // 待支付
            order.setPayExpireTime(msg.getPayExpireTime());
            orderMapper.insertOrder(order);
            logger.info("【秒杀消费者】秒杀订单写库成功，orderNo={}", orderNo);
        } catch (Exception e) {
            logger.error("【秒杀消费者】秒杀订单写库失败，回滚，orderNo={}", orderNo, e);
            // 回滚数据库库存 + 统一订单 + Redis
            try { itemMapper.incrStock(msg.getItemId(), quantity); } catch (Exception ex) {
                logger.error("【秒杀消费者】回滚数据库库存失败，itemId={}", msg.getItemId(), ex);
            }
            cancelOrderAndRollbackRedis(orderNo, msg, quantity);
            ack.acknowledge();
            return;
        }

        // 步骤4：更新 orderKey 为真实 orderNo，前端轮询可拿到订单信息
        try {
            redisTemplate.opsForValue().set(msg.getOrderKey(), orderNo, msg.getExpireSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            // 更新 orderKey 失败不影响主流程，订单已写库，前端可通过数据库查到
            logger.warn("【秒杀消费者】更新 orderKey 失败，orderNo={}, error={}", orderNo, e.getMessage());
        }

        logger.info("【秒杀消费者】订单处理完成，orderNo={}, userId={}", orderNo, msg.getUserId());
        ack.acknowledge();
    }

    /**
     * 仅回滚 Redis（checkout 失败时使用）
     */
    private void rollbackRedis(SeckillOrderMessage msg, int quantity) {
        try {
            stringRedisTemplate.opsForValue().increment(msg.getStockKey(), quantity);
            stringRedisTemplate.opsForValue().increment(msg.getBoughtCntKey(), -quantity);
            redisTemplate.delete(msg.getOrderKey());
        } catch (Exception e) {
            logger.error("【秒杀消费者】回滚 Redis 失败，orderKey={}", msg.getOrderKey(), e);
        }
    }

    /**
     * 取消统一订单 + 回滚 Redis（checkout 成功但后续步骤失败时使用）
     */
    private void cancelOrderAndRollbackRedis(String orderNo, SeckillOrderMessage msg, int quantity) {
        try {
            orderService.cancelPaymentByOrderNo(orderNo);
        } catch (Exception e) {
            logger.error("【秒杀消费者】取消统一订单失败，orderNo={}", orderNo, e);
        }
        rollbackRedis(msg, quantity);
    }
}
