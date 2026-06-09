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
 *   1. 幂等校验：按 seckillNo 判重，已存在则跳过
 *   2. 调用 OrderCheckoutService.checkout() 写 osh_order + 调支付平台，得到 orderNo
 *   3. 扣减数据库库存
 *   4. 写入 osh_seckill_order（seckillNo + orderNo 同时落库）
 *   5. 延长 Redis orderKey 的 TTL 到支付超时时间（value 保持 seckillNo 不变）
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

        // 幂等校验：按 seckillNo 判重，防止 Kafka 消息重复消费
        // 不能再按 activityId+itemId+userId 判重，否则会误杀同一用户的第二次、第三次合法购买
        OshSeckillOrder existOrder = orderMapper.selectOrderBySeckillNo(msg.getSeckillNo());
        if (existOrder != null) {
            logger.warn("【秒杀消费者】seckillNo 已存在，跳过重复消费，seckillNo={}", msg.getSeckillNo());
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
        // 秒杀单落库时机在 checkout 成功之后，order_no 必有值，不存在空值问题
        try {
            OshSeckillOrder order = new OshSeckillOrder();
            order.setSeckillNo(msg.getSeckillNo()); // 秒杀尝试号，来自消息体
            order.setOrderNo(orderNo);              // 统一订单号，来自 checkout 返回
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
            logger.info("【秒杀消费者】秒杀订单写库成功，seckillNo={}, orderNo={}", msg.getSeckillNo(), orderNo);
        } catch (Exception e) {
            logger.error("【秒杀消费者】秒杀订单写库失败，回滚，seckillNo={}, orderNo={}", msg.getSeckillNo(), orderNo, e);
            // 回滚数据库库存 + 统一订单 + Redis
            try { itemMapper.incrStock(msg.getItemId(), quantity); } catch (Exception ex) {
                logger.error("【秒杀消费者】回滚数据库库存失败，itemId={}", msg.getItemId(), ex);
            }
            cancelOrderAndRollbackRedis(orderNo, msg, quantity);
            ack.acknowledge();
            return;
        }

        // 步骤4：延长 orderKey 的 TTL 到真实支付超时时间
        // orderKey 的 value 保持 seckillNo 不变，只刷新过期时间
        // 前端轮询通过 getSeckillResult() 拿到 seckillNo，再查库得到 orderNo
        try {
            redisTemplate.expire(msg.getOrderKey(), msg.getExpireSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            // 延长 TTL 失败不影响主流程，订单已写库，前端可通过数据库查到
            logger.warn("【秒杀消费者】延长 orderKey TTL 失败，seckillNo={}, error={}", msg.getSeckillNo(), e.getMessage());
        }

        logger.info("【秒杀消费者】订单处理完成，seckillNo={}, orderNo={}, userId={}", msg.getSeckillNo(), orderNo, msg.getUserId());
        ack.acknowledge();
    }

    /**
     * 仅回滚 Redis（checkout 失败时使用）
     * 同时删除 orderKey，释放防重复提交锁，允许用户重新下单
     */
    private void rollbackRedis(SeckillOrderMessage msg, int quantity) {
        try {
            stringRedisTemplate.opsForValue().increment(msg.getStockKey(), quantity);
            stringRedisTemplate.opsForValue().increment(msg.getBoughtCntKey(), -quantity);
            redisTemplate.delete(msg.getOrderKey());
            logger.info("【秒杀消费者】Redis 回滚完成，orderKey={} 已释放", msg.getOrderKey());
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
