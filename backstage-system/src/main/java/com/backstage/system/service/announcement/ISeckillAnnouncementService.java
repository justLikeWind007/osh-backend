package com.backstage.system.service.announcement;

import com.backstage.system.domain.vo.seckill.SeckillAnnouncementVO;

import java.util.List;

/**
 * 秒杀公告/动态 Service 接口
 *
 * @author backstage
 * @date 2026-05-22
 */
public interface ISeckillAnnouncementService {

    /**
     * 同步进行中活动的商品信息到公告栏（定时任务调用）
     * 遍历所有进行中活动的商品明细，不存在则插入，已存在则跳过
     */
    void syncSeckillNotices();

    /**
     * 回填历史已支付订单到动态栏（一次性执行）
     * 将 osh_seckill_order 中 status=1 的历史订单写入 osh_announcement
     */
    void backfillSeckillDynamics();

    /**
     * 支付成功时写入一条动态记录（由 SeckillPaidHandler 调用）
     *
     * @param username   脱敏用户名（如：张**）
     * @param goodsTitle 商品标题
     * @param goodsId    商品ID（作为 resourceId）
     */
    void insertSeckillDynamic(String username, String goodsTitle, Long goodsId);

    /**
     * 查询秒杀公告栏数据
     *
     * @param limit 返回条数
     */
    List<SeckillAnnouncementVO> getSeckillNotices(int limit);

    /**
     * 查询秒杀动态栏数据
     *
     * @param limit 返回条数
     */
    List<SeckillAnnouncementVO> getSeckillDynamics(int limit);
}
