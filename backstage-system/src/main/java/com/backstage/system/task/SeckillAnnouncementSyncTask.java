package com.backstage.system.task;

import com.backstage.system.service.announcement.ISeckillAnnouncementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 秒杀公告/动态同步定时任务
 *
 * <p>公告栏：每5分钟扫描一次进行中的活动，将商品信息同步到 osh_announcement（幂等，已存在则跳过）</p>
 * <p>动态栏：应用启动时执行一次历史订单回填，之后由 SeckillPaidHandler 实时写入</p>
 *
 * @author backstage
 * @date 2026-05-22
 */
@Component
public class SeckillAnnouncementSyncTask {

    private static final Logger logger = LoggerFactory.getLogger(SeckillAnnouncementSyncTask.class);

    @Autowired
    private ISeckillAnnouncementService seckillAnnouncementService;

    /**
     * 应用启动后执行一次历史动态回填
     * 将 osh_seckill_order 中已支付的历史订单写入 osh_announcement（幂等，已存在则跳过）
     */
    @PostConstruct
    public void backfillOnStartup() {
        logger.info("【秒杀动态回填】应用启动，开始回填历史已支付订单...");
        try {
            seckillAnnouncementService.backfillSeckillDynamics();
        } catch (Exception e) {
            logger.error("【秒杀动态回填】回填失败，error={}", e.getMessage(), e);
        }
    }

    /**
     * 公告栏同步：每5分钟执行一次
     * 扫描进行中的活动，将商品信息同步到 osh_announcement
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncSeckillNotices() {
        logger.info("【秒杀公告同步】定时任务开始...");
        try {
            seckillAnnouncementService.syncSeckillNotices();
        } catch (Exception e) {
            logger.error("【秒杀公告同步】定时任务异常，error={}", e.getMessage(), e);
        }
    }
}
