package com.backstage.system.service.impl.seckill;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.seckill.OshSeckillActivity;
import com.backstage.system.domain.seckill.OshSeckillActivityItem;
import com.backstage.system.domain.seckill.es.SeckillItemEsDocument;
import com.backstage.system.domain.vo.seckill.SeckillActivityItemVO;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillActivityMapper;
import com.backstage.system.mapper.seckill.SeckillItemEsMapper;
import com.backstage.system.service.seckill.ISeckillItemEsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * 秒杀商品明细 ES 搜索 Service 实现
 */
@Service
public class SeckillItemEsServiceImpl implements ISeckillItemEsService {

    private static final Logger log = LoggerFactory.getLogger(SeckillItemEsServiceImpl.class);

    /** activityStatus=2 表示进行中 */
    private static final int ACTIVITY_STATUS_ONGOING = 2;

    @Autowired
    private SeckillItemEsMapper seckillItemEsMapper;

    @Autowired
    private OshSeckillActivityMapper activityMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Override
    public PageResponse<SeckillActivityItemVO> searchItems(
            String keyword, Integer goodsType, int pageNum, int pageSize) {
        try {
            return seckillItemEsMapper.searchItems(keyword, goodsType, pageNum, pageSize);
        } catch (Exception ex) {
            log.error("search seckill items from es failed, keyword={}, goodsType={}", keyword, goodsType, ex);
            throw new IllegalStateException("search seckill items from es failed", ex);
        }
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

    private SeckillItemEsDocument buildEsDocument(OshSeckillActivityItem item, OshSeckillActivity activity) {
        SeckillItemEsDocument doc = new SeckillItemEsDocument();
        doc.setId(item.getId());
        doc.setActivityId(activity.getId());
        doc.setActivityStatus(activity.getStatus());
        doc.setGoodsId(item.getGoodsId());
        doc.setGoodsType(item.getGoodsType());
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
        return doc;
    }
}
