package com.backstage.system.service.impl.seckill;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.seckill.OshSeckillActivity;
import com.backstage.system.domain.seckill.OshSeckillActivityItem;
import com.backstage.system.domain.seckill.es.SeckillItemEsDocument;
import com.backstage.system.domain.vo.seckill.SeckillActivityItemVO;
import com.backstage.system.domain.vo.seckill.SeckillActivityUserVO;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillActivityMapper;
import com.backstage.system.mapper.seckill.OshSeckillGoodsTagMapper;
import com.backstage.system.mapper.seckill.SeckillItemEsMapper;
import com.backstage.system.service.seckill.ISeckillItemEsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀商品明细 ES 搜索 Service 实现
 *
 * ES 查出平铺的商品明细后，在 Service 层按 activityId 分组，
 * 组装成 SeckillActivityUserVO（活动维度），与 MySQL 路径结构保持一致。
 * 分页粒度为活动。
 */
@Service
public class SeckillItemEsServiceImpl implements ISeckillItemEsService {

    private static final Logger log = LoggerFactory.getLogger(SeckillItemEsServiceImpl.class);

    /** activityStatus=2 表示进行中 */
    private static final int ACTIVITY_STATUS_ONGOING = 2;

    /**
     * ES 单次最大拉取条数。
     * 按活动分页时需要先拉全量商品再分组，这里设一个足够大的上限。
     * 秒杀活动商品数量通常不会很多，1000 条足够覆盖。
     */
    private static final int ES_MAX_FETCH_SIZE = 1000;

    @Autowired
    private SeckillItemEsMapper seckillItemEsMapper;

    @Autowired
    private OshSeckillActivityMapper activityMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private OshSeckillGoodsTagMapper seckillGoodsTagMapper;

    @Override
    public PageResponse<SeckillActivityUserVO> searchActivities(
            String keyword, Integer goodsType, List<String> tagNameList, int pageNum, int pageSize) {
        // 1. 从 ES 一次性拉取所有匹配的商品明细（不分页，后面按活动分页）
        PageResponse<SeckillActivityItemVO> itemPage;
        try {
            itemPage = seckillItemEsMapper.searchItems(keyword, goodsType, tagNameList, 1, ES_MAX_FETCH_SIZE);
        } catch (Exception ex) {
            log.error("search seckill items from es failed, keyword={}, goodsType={}, tagNameList={}", keyword, goodsType, tagNameList, ex);
            throw new IllegalStateException("search seckill items from es failed", ex);
        }

        List<SeckillActivityItemVO> allItems = itemPage.getRows();
        if (allItems == null || allItems.isEmpty()) {
            return PageResponse.of(Collections.emptyList(), 0L, pageNum, pageSize);
        }

        // 2. 按 activityId 分组，保持 ES 返回的顺序（LinkedHashMap 保序）
        Map<Long, List<SeckillActivityItemVO>> groupedByActivity = new LinkedHashMap<>();
        for (SeckillActivityItemVO item : allItems) {
            groupedByActivity
                    .computeIfAbsent(item.getActivityId(), k -> new ArrayList<>())
                    .add(item);
        }

        // 3. 每组构建一个 SeckillActivityUserVO
        List<SeckillActivityUserVO> allActivities = new ArrayList<>(groupedByActivity.size());
        for (Map.Entry<Long, List<SeckillActivityItemVO>> entry : groupedByActivity.entrySet()) {
            SeckillActivityItemVO firstItem = entry.getValue().get(0);
            allActivities.add(buildActivityVO(firstItem, entry.getValue()));
        }

        // 4. 按活动维度分页
        long totalActivities = allActivities.size();
        int fromIndex = Math.max((pageNum - 1) * pageSize, 0);
        if (fromIndex >= allActivities.size()) {
            return PageResponse.of(Collections.emptyList(), totalActivities, pageNum, pageSize);
        }
        int toIndex = Math.min(fromIndex + pageSize, allActivities.size());
        List<SeckillActivityUserVO> pageActivities = new ArrayList<>(allActivities.subList(fromIndex, toIndex));

        return PageResponse.of(pageActivities, totalActivities, pageNum, pageSize);
    }

    @Override
    public int syncAllItemsToEs() {
        // 1. 清空索引
        try {
            seckillItemEsMapper.deleteAllItems();
        } catch (Exception ex) {
            throw new IllegalStateException("clear seckill items in es failed", ex);
        }

        // 2. 查询所有进行中的活动
        OshSeckillActivity query = new OshSeckillActivity();
        query.setStatus(ACTIVITY_STATUS_ONGOING);
        List<OshSeckillActivity> activities = activityMapper.selectActivityList(query);
        if (activities == null || activities.isEmpty()) {
            return 0;
        }

        // 3. 遍历活动，构建文档并批量写入
        int total = 0;
        for (OshSeckillActivity activity : activities) {
            List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(activity.getId());
            if (items == null || items.isEmpty()) {
                continue;
            }
            List<SeckillItemEsDocument> documents = new ArrayList<>(items.size());
            for (OshSeckillActivityItem item : items) {
                documents.add(buildEsDocument(item, activity));
            }
            try {
                total += seckillItemEsMapper.bulkUpsertItems(documents);
            } catch (Exception ex) {
                throw new IllegalStateException(
                        "sync seckill items to es failed, activityId=" + activity.getId(), ex);
            }
        }
        return total;
    }

    // ==================== 私有方法 ====================

    /**
     * 用第一条商品明细里冗余的活动字段 + 该活动所有商品，构建活动维度 VO
     */
    private SeckillActivityUserVO buildActivityVO(
            SeckillActivityItemVO firstItem, List<SeckillActivityItemVO> items) {
        SeckillActivityUserVO vo = new SeckillActivityUserVO();
        vo.setId(firstItem.getActivityId());
        vo.setTitle(firstItem.getActivityTitle());
        vo.setPayTimeoutMin(firstItem.getPayTimeoutMin());
        vo.setStartTime(firstItem.getStartTime());
        vo.setEndTime(firstItem.getEndTime());
        vo.setItems(items);

        // 按当前时间动态推导活动状态，不直接信任 ES 文档里的 activityStatus
        // 避免定时任务延迟导致返回状态不准确
        Date now = new Date();
        if (firstItem.getStartTime() != null && now.before(firstItem.getStartTime())) {
            vo.setStatus(1); // 未开始
        } else if (firstItem.getEndTime() != null && now.before(firstItem.getEndTime())) {
            vo.setStatus(2); // 进行中
        } else {
            vo.setStatus(3); // 已结束
        }
        return vo;
    }

    private SeckillItemEsDocument buildEsDocument(OshSeckillActivityItem item, OshSeckillActivity activity) {
        SeckillItemEsDocument doc = new SeckillItemEsDocument();
        doc.setId(item.getId());
        doc.setActivityId(activity.getId());
        doc.setActivityStatus(activity.getStatus());
        doc.setActivityTitle(activity.getTitle());
        doc.setPayTimeoutMin(activity.getPayTimeoutMin());
        doc.setGoodsId(item.getGoodsId());
        doc.setGoodsType(item.getGoodsType());
        doc.setNo(item.getNo());
        doc.setTitle(item.getTitle());
        doc.setCover(item.getCover());
        doc.setOriginPrice(item.getOriginPrice());
        doc.setSeckillPrice(item.getSeckillPrice());
        doc.setTotalStock(item.getTotalStock());
        doc.setAvailableStock(item.getAvailableStock());
        doc.setSoldCount(item.getSoldCount() != null ? item.getSoldCount() : 0);
        doc.setLimitPerUser(item.getLimitPerUser());
        doc.setSort(item.getSort());
        if (activity.getStartTime() != null) {
            doc.setStartTime(activity.getStartTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (activity.getEndTime() != null) {
            doc.setEndTime(activity.getEndTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        doc.setDeleteFlag(item.getDeleteFlag() != null ? item.getDeleteFlag() : 0);
        if (item.getCreateTime() != null) {
            doc.setCreateTime(item.getCreateTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (item.getUpdateTime() != null) {
            doc.setUpdateTime(item.getUpdateTime().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        // 写入标签
        if (item.getSeckillGoodsId() != null) {
            List<String> tagNames = seckillGoodsTagMapper.selectTagNamesBySeckillGoodsId(item.getSeckillGoodsId());
            doc.setTagNames(tagNames);
            doc.setTagNamesText(tagNames == null || tagNames.isEmpty() ? "" : String.join(" ", tagNames));
        }
        return doc;
    }
}
