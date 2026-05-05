package com.backstage.system.service.impl.seckill;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.dto.seckill.SeckillActivityAddDTO;
import com.backstage.system.domain.dto.seckill.SeckillActivityItemAddDTO;
import com.backstage.system.domain.dto.seckill.SeckillActivityItemUpdateDTO;
import com.backstage.system.domain.dto.seckill.SeckillActivityStatusDTO;
import com.backstage.system.domain.dto.seckill.SeckillActivityUpdateDTO;
import com.backstage.system.domain.seckill.OshSeckillActivity;
import com.backstage.system.domain.seckill.OshSeckillActivityItem;
import com.backstage.system.domain.seckill.OshSeckillGoods;
import com.backstage.system.domain.vo.seckill.SeckillActivityItemVO;
import com.backstage.system.domain.vo.seckill.SeckillActivityUserVO;
import com.backstage.system.domain.vo.seckill.SeckillActivityVO;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillActivityMapper;
import com.backstage.system.mapper.seckill.OshSeckillGoodsMapper;
import com.backstage.system.service.seckill.IOshSeckillActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀活动 Service 实现
 *
 * @author backstage
 * @date 2026-04-28
 */
@Service
public class OshSeckillActivityServiceImpl implements IOshSeckillActivityService {

    @Autowired
    private OshSeckillActivityMapper activityMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private OshSeckillGoodsMapper goodsMapper;

    // ==================== 查询 ====================

    @Override
    public SeckillActivityVO selectActivityById(Long id) {
        OshSeckillActivity activity = activityMapper.selectActivityById(id);
        if (activity == null) return null;
        List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(id);
        return toVO(activity, items);
    }

    @Override
    public List<SeckillActivityVO> selectActivityList(OshSeckillActivity activity) {
        List<OshSeckillActivity> activities = activityMapper.selectActivityList(activity);
        return activities.stream()
                .map(a -> toVO(a, itemMapper.selectItemsByActivityId(a.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<SeckillActivityUserVO> selectActiveActivityList() {
        OshSeckillActivity query = new OshSeckillActivity();
        query.setStatus(2); // 固定只查进行中
        List<OshSeckillActivity> activities = activityMapper.selectActivityList(query);
        return activities.stream()
                .map(a -> toUserVO(a, itemMapper.selectItemsByActivityId(a.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public SeckillActivityUserVO selectActiveActivityById(Long id) {
        OshSeckillActivity activity = activityMapper.selectActivityById(id);
        if (activity == null || activity.getStatus() != 2) {
            return null;
        }
        List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(id);
        return toUserVO(activity, items);
    }

    // ==================== 写操作 ====================

    /**
     * 创建秒杀活动（含商品明细）
     * 1. 校验每个明细的商品池状态和最低秒杀价
     * 2. 插入活动主记录
     * 3. 批量插入明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertActivity(SeckillActivityAddDTO dto) {
        // 校验并构建明细列表
        List<OshSeckillActivityItem> items = buildItemsFromAddDTO(dto.getItems(), null);

        // 插入活动主记录
        OshSeckillActivity activity = new OshSeckillActivity();
        activity.setTitle(dto.getTitle());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setPayTimeoutMin(dto.getPayTimeoutMin());
        activity.setStatus(0); // 默认草稿
        activityMapper.insertActivity(activity);

        // 回填 activityId 并批量插入明细
        Long activityId = activity.getId();
        items.forEach(item -> item.setActivityId(activityId));
        itemMapper.insertItems(items);

        return 1;
    }

    /**
     * 修改秒杀活动（仅草稿状态可修改）
     * items 传入则全量替换：先逻辑删除旧明细，再插入新明细
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateActivity(SeckillActivityUpdateDTO dto) {
        OshSeckillActivity exist = activityMapper.selectActivityById(dto.getId());
        if (exist == null) {
            throw new ServiceException("秒杀活动不存在");
        }
        if (exist.getStatus() != 0) {
            throw new ServiceException("只有草稿状态的活动才可以修改");
        }

        // 更新活动主记录
        OshSeckillActivity activity = new OshSeckillActivity();
        activity.setId(dto.getId());
        activity.setTitle(dto.getTitle());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setPayTimeoutMin(dto.getPayTimeoutMin());
        activityMapper.updateActivity(activity);

        // 全量替换明细
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            // 逻辑删除旧明细
            itemMapper.deleteItemsByActivityId(dto.getId());
            // 构建并插入新明细
            List<OshSeckillActivityItem> newItems = buildItemsFromUpdateDTO(dto.getItems(), dto.getId());
            itemMapper.insertItems(newItems);
        }

        return 1;
    }

    /**
     * 批量发布/下架活动
     * 状态流转规则：
     * 草稿(0)   → 发布(1-未开始)：只有草稿才能发布
     * 未开始(1) → 下架(4)
     * 进行中(2) → 下架(4)
     * 已结束(3)、已下架(4) 不可操作
     */
    @Override
    public int updateActivityStatus(SeckillActivityStatusDTO dto) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            throw new ServiceException("活动ID列表不能为空");
        }
        Integer targetStatus = dto.getStatus();
        for (Long id : dto.getIds()) {
            OshSeckillActivity exist = activityMapper.selectActivityById(id);
            if (exist == null) {
                throw new ServiceException("活动ID " + id + " 不存在");
            }
            Integer currentStatus = exist.getStatus();
            if (currentStatus == 3 || currentStatus == 4) {
                throw new ServiceException("活动【" + exist.getTitle() + "】已结束或已下架，不可操作");
            }
            if (targetStatus == 1 && currentStatus != 0) {
                throw new ServiceException("活动【" + exist.getTitle() + "】不是草稿状态，无法发布");
            }
            if (targetStatus == 4 && currentStatus != 1 && currentStatus != 2) {
                throw new ServiceException("活动【" + exist.getTitle() + "】不是未开始或进行中状态，无法下架");
            }
        }
        return activityMapper.updateActivityStatusByIds(dto.getIds(), targetStatus);
    }

    /**
     * 批量逻辑删除活动（同时删除明细）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteActivityByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("活动ID列表不能为空");
        }
        // 逐个删除明细
        ids.forEach(itemMapper::deleteItemsByActivityId);
        return activityMapper.deleteActivityByIds(ids);
    }

    // ==================== 私有方法 ====================

    /**
     * 根据 AddDTO 的 items 构建明细实体列表（含商品池校验）
     */
    private List<OshSeckillActivityItem> buildItemsFromAddDTO(
            List<SeckillActivityItemAddDTO> dtoItems, Long activityId) {
        List<OshSeckillActivityItem> result = new ArrayList<>();
        for (SeckillActivityItemAddDTO dto : dtoItems) {
            OshSeckillGoods goods = goodsMapper.selectSeckillGoodsById(dto.getSeckillGoodsId());
            if (goods == null) {
                throw new ServiceException("秒杀商品池ID " + dto.getSeckillGoodsId() + " 不存在");
            }
            if (goods.getStatus() != 1) {
                throw new ServiceException("商品【" + goods.getGoodsName() + "】未上架，无法加入活动");
            }
            if (dto.getSeckillPrice().compareTo(goods.getMinSeckillPrice()) < 0) {
                throw new ServiceException("商品【" + goods.getGoodsName() + "】秒杀价不能低于最低秒杀价：" + goods.getMinSeckillPrice());
            }
            OshSeckillActivityItem item = new OshSeckillActivityItem();
            item.setActivityId(activityId);
            item.setSeckillGoodsId(dto.getSeckillGoodsId());
            item.setGoodsId(goods.getGoodsId());
            item.setGoodsType(goods.getGoodsType());
            item.setTitle(dto.getTitle() != null ? dto.getTitle() : goods.getGoodsName());
            item.setCover(dto.getCover() != null ? dto.getCover() : goods.getGoodsCover());
            item.setOriginPrice(goods.getOriginPrice());
            item.setSeckillPrice(dto.getSeckillPrice());
            item.setTotalStock(dto.getTotalStock());
            item.setAvailableStock(dto.getTotalStock()); // 初始可用库存 = 总库存
            item.setLimitPerUser(dto.getLimitPerUser());
            item.setSort(dto.getSort() != null ? dto.getSort() : 0);
            result.add(item);
        }
        return result;
    }

    /**
     * 根据 UpdateDTO 的 items 构建明细实体列表（含商品池校验）
     */
    private List<OshSeckillActivityItem> buildItemsFromUpdateDTO(
            List<SeckillActivityItemUpdateDTO> dtoItems, Long activityId) {
        List<OshSeckillActivityItem> result = new ArrayList<>();
        for (SeckillActivityItemUpdateDTO dto : dtoItems) {
            OshSeckillGoods goods = goodsMapper.selectSeckillGoodsById(dto.getSeckillGoodsId());
            if (goods == null) {
                throw new ServiceException("秒杀商品池ID " + dto.getSeckillGoodsId() + " 不存在");
            }
            if (goods.getStatus() != 1) {
                throw new ServiceException("商品【" + goods.getGoodsName() + "】未上架，无法加入活动");
            }
            if (dto.getSeckillPrice() != null && dto.getSeckillPrice().compareTo(goods.getMinSeckillPrice()) < 0) {
                throw new ServiceException("商品【" + goods.getGoodsName() + "】秒杀价不能低于最低秒杀价：" + goods.getMinSeckillPrice());
            }
            OshSeckillActivityItem item = new OshSeckillActivityItem();
            item.setActivityId(activityId);
            item.setSeckillGoodsId(dto.getSeckillGoodsId());
            item.setGoodsId(goods.getGoodsId());
            item.setGoodsType(goods.getGoodsType());
            item.setTitle(dto.getTitle() != null ? dto.getTitle() : goods.getGoodsName());
            item.setCover(dto.getCover() != null ? dto.getCover() : goods.getGoodsCover());
            item.setOriginPrice(goods.getOriginPrice());
            item.setSeckillPrice(dto.getSeckillPrice() != null ? dto.getSeckillPrice() : goods.getMinSeckillPrice());
            item.setTotalStock(dto.getTotalStock());
            item.setAvailableStock(dto.getTotalStock());
            item.setLimitPerUser(dto.getLimitPerUser());
            item.setSort(dto.getSort() != null ? dto.getSort() : 0);
            result.add(item);
        }
        return result;
    }

    /** 活动 + 明细列表 → 管理端 VO */
    private SeckillActivityVO toVO(OshSeckillActivity activity, List<OshSeckillActivityItem> items) {
        SeckillActivityVO vo = new SeckillActivityVO();
        vo.setId(activity.getId());
        vo.setTitle(activity.getTitle());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setStatus(activity.getStatus());
        vo.setPayTimeoutMin(activity.getPayTimeoutMin());
        vo.setCreateTime(activity.getCreateTime());
        vo.setUpdateTime(activity.getUpdateTime());
        vo.setItems(items.stream().map(this::toItemVO).collect(Collectors.toList()));
        return vo;
    }

    /** 活动 + 明细列表 → 用户端 VO */
    private SeckillActivityUserVO toUserVO(OshSeckillActivity activity, List<OshSeckillActivityItem> items) {
        SeckillActivityUserVO vo = new SeckillActivityUserVO();
        vo.setId(activity.getId());
        vo.setTitle(activity.getTitle());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setStatus(activity.getStatus());
        vo.setPayTimeoutMin(activity.getPayTimeoutMin());
        vo.setItems(items.stream().map(this::toItemVO).collect(Collectors.toList()));
        return vo;
    }

    /** 明细实体 → 明细 VO */
    private SeckillActivityItemVO toItemVO(OshSeckillActivityItem item) {
        SeckillActivityItemVO vo = new SeckillActivityItemVO();
        vo.setId(item.getId());
        vo.setActivityId(item.getActivityId());
        vo.setSeckillGoodsId(item.getSeckillGoodsId());
        vo.setGoodsId(item.getGoodsId());
        vo.setGoodsType(item.getGoodsType());
        vo.setTitle(item.getTitle());
        vo.setCover(item.getCover());
        vo.setOriginPrice(item.getOriginPrice());
        vo.setSeckillPrice(item.getSeckillPrice());
        vo.setTotalStock(item.getTotalStock());
        vo.setAvailableStock(item.getAvailableStock());
        vo.setSoldCount(item.getSoldCount());
        vo.setLimitPerUser(item.getLimitPerUser());
        vo.setSort(item.getSort());
        return vo;
    }
}
