package com.backstage.framework.task;

import com.backstage.common.constant.SeckillCacheConstants;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Lua 脚本：归还 Redis 库存时不超过 totalStock 上限（原子操作）
     * KEYS[1] = stockKey
     * ARGV[1] = 归还数量 qty
     * ARGV[2] = totalStock 上限
     */
    private static final DefaultRedisScript<Long> RETURN_STOCK_SCRIPT = new DefaultRedisScript<>(
            "local cur = tonumber(redis.call('GET', KEYS[1])) or 0 " +
            "local total = tonumber(ARGV[2]) " +
            "local qty   = tonumber(ARGV[1]) " +
            "local newVal = math.min(cur + qty, total) " +
            "redis.call('SET', KEYS[1], tostring(newVal)) " +
            "return newVal",
            Long.class
    );

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

                // 2. 归还 Redis 库存（Lua 原子操作，归还后不超过 totalStock）
                String stockKey     = SECKILL_STOCK_KEY     + order.getActivityId() + ":" + order.getItemId();
                String boughtCntKey = SECKILL_BOUGHT_CNT_KEY + order.getActivityId() + ":" + order.getItemId() + ":" + order.getUserId();
                String orderKey     = SECKILL_ORDER_KEY     + order.getActivityId() + ":" + order.getItemId() + ":" + order.getUserId();

                int qty = order.getQuantity() != null ? order.getQuantity() : 1;

                // 2a. 同步归还数据库库存（available_stock 不超过 total_stock，sold_count 不低于 0）
                itemMapper.incrStock(order.getItemId(), qty);

                // 2b. 归还 Redis 库存，需要 totalStock 作为上限
                //     先从数据库取最新 totalStock，再用 Lua 脚本原子归还
                com.backstage.system.domain.seckill.OshSeckillActivityItem item =
                        itemMapper.selectItemById(order.getItemId());
                if (item != null && Boolean.TRUE.equals(stringRedisTemplate.hasKey(stockKey))) {
                    stringRedisTemplate.execute(
                            RETURN_STOCK_SCRIPT,
                            Arrays.asList(stockKey),
                            String.valueOf(qty),
                            String.valueOf(item.getTotalStock())
                    );
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
