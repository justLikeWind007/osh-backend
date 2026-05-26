package com.backstage.system.task;

import com.backstage.system.domain.seckill.OshSeckillActivity;
import com.backstage.system.domain.seckill.OshSeckillActivityItem;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillActivityMapper;
import com.backstage.system.service.OutboxEventService;
import com.backstage.system.service.seckill.SeckillItemIndexDeleteMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 秒杀 ES 过期文档清理定时任务（方式B 兜底）
 *
 * <p>查询时已通过 endTime > now 过滤（方式A）保证结果准确，
 * 本任务作为索引维护手段，每天凌晨清理已结束超过 1 天的活动的 ES 文档，
 * 防止索引长期膨胀。</p>
 *
 * @author backstage
 */
@Component
public class SeckillItemEsCleanTask {

    private static final Logger logger = LoggerFactory.getLogger(SeckillItemEsCleanTask.class);

    /** 活动结束超过此天数后，才触发 ES 文档清理 */
    private static final int CLEAN_AFTER_END_DAYS = 1;

    @Autowired
    private OshSeckillActivityMapper activityMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private OutboxEventService outboxEventService;

    /**
     * 每天凌晨 2 点执行一次
     * 扫描已结束（status=3）且结束时间超过 CLEAN_AFTER_END_DAYS 天的活动，
     * 对其明细发送 DELETE 事件，由 Flink 从 ES 删除文档
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredItems() {
        logger.info("【秒杀ES清理】定时任务开始，清理已结束超过 {} 天的活动文档...", CLEAN_AFTER_END_DAYS);
        try {
            doClean();
        } catch (Exception e) {
            logger.error("【秒杀ES清理】定时任务异常，error={}", e.getMessage(), e);
        }
    }

    private void doClean() {
        // 查询所有已结束的活动（status=3）
        OshSeckillActivity query = new OshSeckillActivity();
        query.setStatus(3);
        List<OshSeckillActivity> finishedActivities = activityMapper.selectActivityList(query);

        if (finishedActivities == null || finishedActivities.isEmpty()) {
            logger.info("【秒杀ES清理】无已结束活动，跳过");
            return;
        }

        Date now = new Date();
        long cleanThresholdMs = (long) CLEAN_AFTER_END_DAYS * 24 * 60 * 60 * 1000;
        int totalCleaned = 0;

        for (OshSeckillActivity activity : finishedActivities) {
            // 只清理结束时间超过阈值的活动
            if (activity.getEndTime() == null) continue;
            long endedAgoMs = now.getTime() - activity.getEndTime().getTime();
            if (endedAgoMs < cleanThresholdMs) continue;

            List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(activity.getId());
            if (items == null || items.isEmpty()) continue;

            for (OshSeckillActivityItem item : items) {
                outboxEventService.saveSeckillItemIndexDeleteEvent(
                        item.getId(),
                        new SeckillItemIndexDeleteMessage(item.getId()),
                        "seckill-es-clean-task");
                totalCleaned++;
            }
            logger.info("【秒杀ES清理】活动ID={}, 标题={}, 明细数={}, 已发送 DELETE 事件",
                    activity.getId(), activity.getTitle(), items.size());
        }

        logger.info("【秒杀ES清理】本轮共发送 DELETE 事件 {} 条", totalCleaned);
    }
}
