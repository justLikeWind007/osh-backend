package com.backstage.framework.task;

import com.backstage.common.constant.SeckillCacheConstants;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 秒杀订单支付超时自动取消定时任务
 * 每分钟扫描一次 status=0 且 pay_expire_time < now 的订单：
 *   1. 更新订单状态为已取消（status=2）
 *   2. 归还 Redis 库存（seckill:stock）
 *   3. 减少用户已购数量（seckill:bought_cnt），允许用户重新下单
 *   4. 删除流程状态 Key（seckill:order），解除重复提交拦截
 *
 * @author backstage
 * @date 2026-05-10
 */
@Component
public class SeckillOrderTimeoutTask {

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderTimeoutTask.class);

    private static final String SECKILL_STOCK_KEY     = SeckillCacheConstants.SECKILL_STOCK_KEY;
    private static final String SECKILL_BOUGHT_CNT_KEY = SeckillCacheConstants.SECKILL_BOUGHT_CNT_KEY;
    private static final String SECKILL_ORDER_KEY      = SeckillCacheConstants.SECKILL_ORDER_KEY;

    @Autowired
    private OshSeckillOrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 每分钟执行一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void cancelTimeoutOrders() {
        List<OshSeckillOrder> timeoutOrders = orderMapper.selectTimeoutOrders();
        if (timeoutOrders == null || timeoutOrders.isEmpty()) {
            return;
        }

        logger.info("【超时取消】扫描到 {} 个超时未支付订单，开始处理", timeoutOrders.size());

        for (OshSeckillOrder order : timeoutOrders) {
            try {
                // 1. 更新订单状态为已取消
                OshSeckillOrder update = new OshSeckillOrder();
                update.setId(order.getId());
                update.setStatus(2);
                update.setCancelTime(new Date());
                update.setCancelReason("pay_timeout");
                orderMapper.updateOrder(update);

                // 2. 归还 Redis 库存
                String stockKey     = SECKILL_STOCK_KEY     + order.getActivityId() + ":" + order.getItemId();
                String boughtCntKey = SECKILL_BOUGHT_CNT_KEY + order.getActivityId() + ":" + order.getItemId() + ":" + order.getUserId();
                String orderKey     = SECKILL_ORDER_KEY     + order.getActivityId() + ":" + order.getItemId() + ":" + order.getUserId();

                int qty = order.getQuantity() != null ? order.getQuantity() : 1;

                // 只在 key 存在时归还，避免活动已结束缓存被清理后重新写入脏数据
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(stockKey))) {
                    stringRedisTemplate.opsForValue().increment(stockKey, qty);
                }

                // 3. 减少用户已购数量，允许用户重新下单
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(boughtCntKey))) {
                    stringRedisTemplate.opsForValue().increment(boughtCntKey, -qty);
                }

                // 4. 删除流程状态 Key（超时后通常已自然过期，显式删除兜底）
                stringRedisTemplate.delete(orderKey);

                logger.info("【超时取消】订单已取消并清理 Redis，seckillNo={}, userId={}, activityId={}, itemId={}",
                        order.getSeckillNo(), order.getUserId(), order.getActivityId(), order.getItemId());

            } catch (Exception e) {
                logger.error("【超时取消】处理订单异常，seckillNo={}", order.getSeckillNo(), e);
            }
        }
    }
}
