package com.backstage.framework.task;

import com.alibaba.fastjson2.JSON;
import com.backstage.system.domain.seckill.OshSeckillActivity;
import com.backstage.system.domain.seckill.OshSeckillActivityItem;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀活动状态自动流转定时任务
 * 使用 Spring @Scheduled，每分钟整点执行：
 *   未开始(1) → 进行中(2)：更新状态 + 预热 Redis 缓存
 *   进行中(2) → 已结束(3)：更新状态 + 清理 Redis 缓存
 *
 * @author backstage
 * @date 2026-05-07
 */
@Component
public class SeckillActivityTask {

    private static final Logger logger = LoggerFactory.getLogger(SeckillActivityTask.class);

    private static final String SECKILL_ACTIVITY_KEY = "seckill:activity:";
    private static final String SECKILL_ITEM_KEY     = "seckill:item:";
    private static final String SECKILL_STOCK_KEY    = "seckill:stock:";
    private static final long   CACHE_EXPIRE         = 7200L;

    @Autowired
    private OshSeckillActivityMapper activityMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 每分钟整点执行，自动流转活动状态
     */
    //@Scheduled(cron = "0/5 * * * * ?")
    public void updateActivityStatus() {
        // ===== 1. 未开始 → 进行中 =====
        List<OshSeckillActivity> toStart = activityMapper.selectActivitiesToStart();
        int startCount = activityMapper.updateToOngoing();
        if (startCount > 0) {
            logger.info("【秒杀定时任务】{}个活动状态更新为进行中，开始预热缓存", startCount);
            toStart.forEach(activity -> {
                OshSeckillActivity latest = activityMapper.selectActivityById(activity.getId());
                if (latest == null) return;

                // 预热活动缓存（status=2）
                redisTemplate.opsForValue().set(
                        SECKILL_ACTIVITY_KEY + latest.getId(),
                        latest, CACHE_EXPIRE, TimeUnit.SECONDS);

                // 预热明细缓存 + 库存 Key
                List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(latest.getId());
                for (OshSeckillActivityItem item : items) {
                    redisTemplate.opsForValue().set(
                            SECKILL_ITEM_KEY + item.getId(),
                            item, CACHE_EXPIRE, TimeUnit.SECONDS);

                    String stockKey = SECKILL_STOCK_KEY + latest.getId() + ":" + item.getId();
                    stringRedisTemplate.opsForValue().setIfAbsent(
                            stockKey,
                            String.valueOf(item.getAvailableStock()),
                            CACHE_EXPIRE, TimeUnit.SECONDS);
                }
                logger.info("【秒杀定时任务】活动{}缓存预热完成，明细数量：{}", latest.getId(), items.size());
            });
        }

        // ===== 2. 进行中 → 已结束 =====
        List<OshSeckillActivity> toEnd = activityMapper.selectActivitiesToEnd();
        int endCount = activityMapper.updateToFinished();
        if (endCount > 0) {
            logger.info("【秒杀定时任务】{}个活动状态更新为已结束，清理缓存", endCount);
            toEnd.forEach(activity -> {
                redisTemplate.delete(SECKILL_ACTIVITY_KEY + activity.getId());
                List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(activity.getId());
                for (OshSeckillActivityItem item : items) {
                    redisTemplate.delete(SECKILL_ITEM_KEY + item.getId());
                    stringRedisTemplate.delete(SECKILL_STOCK_KEY + activity.getId() + ":" + item.getId());
                }
                logger.info("【秒杀定时任务】活动{}缓存已清理", activity.getId());
            });
        }
    }
}
