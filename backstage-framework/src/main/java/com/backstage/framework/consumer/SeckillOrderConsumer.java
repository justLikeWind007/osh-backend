package com.backstage.framework.consumer;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.constant.KafkaConstants;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import com.backstage.system.service.impl.seckill.OshSeckillOrderServiceImpl.SeckillOrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单创建 Kafka 消费者
 * 消费 seckill.order.create Topic，将秒杀订单写入数据库，并同步扣减明细表库存
 *
 * @author backstage
 * @date 2026-04-28
 */
@Component
public class SeckillOrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderConsumer.class);

    @Autowired
    private OshSeckillOrderMapper orderMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @KafkaListener(topics = KafkaConstants.SECKILL_ORDER_CREATE_TOPIC,
                   groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSeckillOrder(String message, Acknowledgment ack) {
        logger.info("【秒杀消费者】收到订单创建消息：{}", message);
        try {
            SeckillOrderMessage msg = JSON.parseObject(message, SeckillOrderMessage.class);

            // 幂等校验：订单号已存在则跳过（防止重复消费）
            OshSeckillOrder exist = orderMapper.selectOrderBySeckillNo(msg.getSeckillNo());
            if (exist != null) {
                logger.warn("【秒杀消费者】订单已存在，跳过重复消费，seckillNo={}", msg.getSeckillNo());
                ack.acknowledge(); // 手动提交
                return;
            }

            // 1. 同步扣减明细表库存（available_stock -1，sold_count +1）
            int quantity = msg.getQuantity() != null ? msg.getQuantity() : 1;
            int affected = itemMapper.decrStock(msg.getItemId(), quantity);
            if (affected == 0) {
                // 数据库库存扣减失败，回滚 Redis 库存
                String stockKey = "seckill:stock:" + msg.getActivityId() + ":" + msg.getItemId();
                stringRedisTemplate.opsForValue().increment(stockKey, quantity);
                logger.error("【秒杀消费者】数据库库存不足，已回滚Redis库存，itemId={}, seckillNo={}",
                        msg.getItemId(), msg.getSeckillNo());
                ack.acknowledge(); // 手动提交（业务失败但消息已处理）
                return; // 不创建订单
            }
            logger.info("【秒杀消费者】数据库库存扣减成功，itemId={}, quantity={}",
                    msg.getItemId(), quantity);

            // 2. 写入订单表
            OshSeckillOrder order = new OshSeckillOrder();
            order.setSeckillNo(msg.getSeckillNo());
            order.setActivityId(msg.getActivityId());
            order.setItemId(msg.getItemId());
            order.setUserId(msg.getUserId());
            order.setGoodsId(msg.getGoodsId());
            order.setGoodsType(msg.getGoodsType());
            order.setGoodsTitle(msg.getGoodsTitle());
            order.setGoodsCover(msg.getGoodsCover());
            order.setOriginPrice(msg.getOriginPrice());
            order.setSeckillPrice(msg.getSeckillPrice());
            order.setQuantity(msg.getQuantity());
            order.setStatus(0); // 待支付
            order.setPayExpireTime(msg.getPayExpireTime());

            orderMapper.insertOrder(order);
            logger.info("【秒杀消费者】订单创建成功，seckillNo={}, userId={}",
                    msg.getSeckillNo(), msg.getUserId());

            ack.acknowledge(); // 手动提交（成功）

        } catch (Exception e) {
            logger.error("【秒杀消费者】订单创建失败，message={}，错误：{}", message, e.getMessage(), e);
            // 不调用 ack.acknowledge()，消息会重新消费
        }
    }
}
