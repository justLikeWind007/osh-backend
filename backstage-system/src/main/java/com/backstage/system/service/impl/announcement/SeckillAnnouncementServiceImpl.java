package com.backstage.system.service.impl.announcement;

import com.backstage.system.domain.seckill.OshSeckillActivity;
import com.backstage.system.domain.seckill.OshSeckillActivityItem;
import com.backstage.system.domain.vo.seckill.SeckillAnnouncementVO;
import com.backstage.system.mapper.announcement.OshAnnouncementMapper;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillActivityMapper;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.announcement.ISeckillAnnouncementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 秒杀公告/动态 Service 实现
 *
 * @author backstage
 * @date 2026-05-22
 */
@Service
public class SeckillAnnouncementServiceImpl implements ISeckillAnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillAnnouncementServiceImpl.class);

    /** 公告栏：秒杀商品公告 */
    private static final String BIZ_TYPE_NOTICE  = "seckill_notice";
    /** 动态栏：用户购买动态 */
    private static final String BIZ_TYPE_DYNAMIC = "seckill_dynamic";

    /** 公告栏图标与颜色 */
    private static final String NOTICE_ICON       = "🔥";
    private static final String NOTICE_ICON_COLOR = "#ef4444";

    /** 动态栏图标与颜色 */
    private static final String DYNAMIC_ICON       = "💳";
    private static final String DYNAMIC_ICON_COLOR = "#10b981";

    @Autowired
    private OshAnnouncementMapper announcementMapper;

    @Autowired
    private OshSeckillActivityMapper activityMapper;

    @Autowired
    private OshSeckillActivityItemMapper activityItemMapper;

    @Autowired
    private OshSeckillOrderMapper orderMapper;

    @Autowired
    private OshUserMapper userMapper;

    // ==================== 公告栏同步 ====================

    /**
     * 同步进行中活动的商品信息到公告栏
     * 遍历所有进行中（status=2）且未删除的活动，取其商品明细，
     * 按 link 去重，不存在则插入，已存在则跳过。
     */
    @Override
    public void syncSeckillNotices() {
        // 查询所有进行中的活动
        OshSeckillActivity query = new OshSeckillActivity();
        query.setStatus(2); // 进行中
        List<OshSeckillActivity> activities = activityMapper.selectActivityList(query);

        if (activities == null || activities.isEmpty()) {
            logger.info("【秒杀公告同步】当前无进行中的活动，跳过");
            return;
        }

        int insertCount = 0;
        int skipCount   = 0;

        for (OshSeckillActivity activity : activities) {
            List<OshSeckillActivityItem> items =
                    activityItemMapper.selectItemsByActivityId(activity.getId());

            if (items == null || items.isEmpty()) {
                continue;
            }

            for (OshSeckillActivityItem item : items) {
                String link = buildNoticeLink(activity.getId(), item.getId());

                // 幂等：已存在则跳过
                int exists = announcementMapper.countSeckillNoticeByActivityAndLink(
                        activity.getId(), link);
                if (exists > 0) {
                    skipCount++;
                    continue;
                }

                String title = buildNoticeTitle(item);
                int sort = item.getSort() != null ? item.getSort() : 0;

                announcementMapper.insertSeckillAnnouncement(
                        title, link, NOTICE_ICON, NOTICE_ICON_COLOR,
                        activity.getId(), sort, BIZ_TYPE_NOTICE);
                insertCount++;
            }
        }

        logger.info("【秒杀公告同步】完成，新增={}，跳过={}", insertCount, skipCount);
    }

    // ==================== 动态栏回填 ====================

    /**
     * 回填历史已支付订单到动态栏（一次性执行，定时任务首次运行时触发）
     * 查询 osh_seckill_order 中 status=1 的订单，关联用户表脱敏，
     * 按 title 去重后批量写入 osh_announcement。
     */
    @Override
    public void backfillSeckillDynamics() {
        // 查询所有已支付订单（不限条数，回填用）
        List<com.backstage.system.domain.vo.seckill.SeckillRecentOrderVO> paidOrders =
                orderMapper.selectRecentPaidOrders(500);

        if (paidOrders == null || paidOrders.isEmpty()) {
            logger.info("【秒杀动态回填】无历史已支付订单，跳过");
            return;
        }

        int insertCount = 0;
        int skipCount   = 0;

        for (com.backstage.system.domain.vo.seckill.SeckillRecentOrderVO order : paidOrders) {
            String title = buildDynamicTitle(order.getUsername(), order.getGoodsTitle());

            // 幂等：已存在则跳过
            int exists = announcementMapper.countSeckillDynamicByTitle(title);
            if (exists > 0) {
                skipCount++;
                continue;
            }

            announcementMapper.insertSeckillAnnouncement(
                    title, "", DYNAMIC_ICON, DYNAMIC_ICON_COLOR,
                    0L, 0, BIZ_TYPE_DYNAMIC);
            insertCount++;
        }

        logger.info("【秒杀动态回填】完成，新增={}，跳过={}", insertCount, skipCount);
    }

    // ==================== 支付成功写入动态 ====================

    /**
     * 支付成功时写入一条动态记录
     */
    @Override
    public void insertSeckillDynamic(String username, String goodsTitle, Long goodsId) {
        String title = buildDynamicTitle(username, goodsTitle);
        try {
            announcementMapper.insertSeckillAnnouncement(
                    title, "", DYNAMIC_ICON, DYNAMIC_ICON_COLOR,
                    goodsId != null ? goodsId : 0L, 0, BIZ_TYPE_DYNAMIC);
            logger.info("【秒杀动态写入】成功，title={}", title);
        } catch (Exception e) {
            // 动态写入失败不影响主流程
            logger.error("【秒杀动态写入】失败，title={}, error={}", title, e.getMessage());
        }
    }

    // ==================== 查询 ====================

    @Override
    public List<SeckillAnnouncementVO> getSeckillNotices(int limit) {
        int safeLimit = (limit <= 0 || limit > 50) ? 10 : limit;
        return announcementMapper.selectSeckillNotices(safeLimit);
    }

    @Override
    public List<SeckillAnnouncementVO> getSeckillDynamics(int limit) {
        int safeLimit = (limit <= 0 || limit > 50) ? 10 : limit;
        return announcementMapper.selectSeckillDynamics(safeLimit);
    }

    // ==================== 私有工具方法 ====================

    /**
     * 构建公告栏标题
     * 格式：「Java零基础入门到精通」限时秒杀中，原价¥299 → ¥9.9
     */
    private String buildNoticeTitle(OshSeckillActivityItem item) {
        String goodsTitle   = item.getTitle() != null ? item.getTitle() : "未知商品";
        String originPrice  = item.getOriginPrice()  != null
                ? item.getOriginPrice().stripTrailingZeros().toPlainString()  : "?";
        String seckillPrice = item.getSeckillPrice() != null
                ? item.getSeckillPrice().stripTrailingZeros().toPlainString() : "?";
        return String.format("「%s」限时秒杀中，原价¥%s → ¥%s", goodsTitle, originPrice, seckillPrice);
    }

    /**
     * 构建动态栏标题
     * 格式：张** 刚刚购买了「Java零基础入门到精通」
     */
    private String buildDynamicTitle(String username, String goodsTitle) {
        String safeUser  = username   != null ? username   : "某用户";
        String safeGoods = goodsTitle != null ? goodsTitle : "某商品";
        return String.format("%s 刚刚购买了「%s」", safeUser, safeGoods);
    }

    /**
     * 构建公告栏跳转链接（用于幂等去重 key）
     * 格式：/seckill/detail/{activityId}/item/{itemId}
     */
    private String buildNoticeLink(Long activityId, Long itemId) {
        return "/seckill/detail/" + activityId + "/item/" + itemId;
    }
}
