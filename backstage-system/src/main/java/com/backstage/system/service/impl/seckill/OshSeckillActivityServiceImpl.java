package com.backstage.system.service.impl.seckill;

import com.backstage.common.constant.SeckillCacheConstants;
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
import com.backstage.system.service.OutboxEventService;
import com.backstage.system.service.seckill.IOshSeckillActivityService;
import com.backstage.system.service.seckill.SeckillItemIndexDeleteMessage;
import com.backstage.system.service.seckill.SeckillItemIndexEventType;
import com.backstage.system.service.seckill.SeckillItemIndexUpsertMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 秒杀活动 Service 实现
 *
 * @author backstage
 * @date 2026-04-28
 */
@Service
public class OshSeckillActivityServiceImpl implements IOshSeckillActivityService {

    private static final Logger logger = LoggerFactory.getLogger(OshSeckillActivityServiceImpl.class);

    private static final String SECKILL_ACTIVITY_KEY = SeckillCacheConstants.SECKILL_ACTIVITY_KEY;
    private static final String SECKILL_ITEM_KEY     = SeckillCacheConstants.SECKILL_ITEM_KEY;
    private static final String SECKILL_STOCK_KEY    = SeckillCacheConstants.SECKILL_STOCK_KEY;
    private static final String SECKILL_BOUGHT_KEY   = SeckillCacheConstants.SECKILL_BOUGHT_KEY;

    @Autowired
    private OshSeckillActivityMapper activityMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private OshSeckillGoodsMapper goodsMapper;

    @Autowired
    private OutboxEventService outboxEventService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
    public List<SeckillActivityUserVO> selectActiveActivityList(String title, Integer goodsType) {
        List<Long> activityIds;
        Date now = new Date();
        // 有搜索条件时，先从明细表找符合条件的活动ID
        if ((title != null && !title.isEmpty()) || goodsType != null) {
            activityIds = itemMapper.selectActiveActivityIdsByCondition(title, goodsType);
            if (activityIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }
        } else {
            // 无搜索条件，查所有已发布（非草稿、非下架）的活动，再用时间过滤
            OshSeckillActivity query = new OshSeckillActivity();
            activityIds = activityMapper.selectActivityList(query)
                    .stream()
                    .filter(a -> a.getStatus() != 0 && a.getStatus() != 4) // 排除草稿和下架
                    .map(OshSeckillActivity::getId)
                    .collect(Collectors.toList());
            if (activityIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }
        }
        // 根据活动ID列表查活动详情 + 明细，以时间窗口判断是否进行中
        return activityIds.stream()
                .map(id -> {
                    OshSeckillActivity activity = activityMapper.selectActivityById(id);
                    if (activity == null) return null;
                    // 下架直接跳过
                    if (activity.getStatus() == 4) return null;
                    // 时间窗口判断
                    if (activity.getStartTime() == null || now.before(activity.getStartTime())) return null;
                    if (activity.getEndTime() == null || now.after(activity.getEndTime())) return null;
                    List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(id);
                    // 有搜索条件时，明细只展示匹配的商品
                    if ((title != null && !title.isEmpty()) || goodsType != null) {
                        items = items.stream()
                                .filter(item -> {
                                    boolean match = true;
                                    if (title != null && !title.isEmpty()) {
                                        match = item.getTitle() != null && item.getTitle().contains(title);
                                    }
                                    if (match && goodsType != null) {
                                        match = goodsType.equals(item.getGoodsType());
                                    }
                                    return match;
                                })
                                .collect(Collectors.toList());
                    }
                    if (items.isEmpty()) return null;
                    return toUserVO(activity, items);
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
    }

    @Override
    public SeckillActivityUserVO selectActiveActivityById(Long id) {
        OshSeckillActivity activity = activityMapper.selectActivityById(id);
        if (activity == null || activity.getStatus() == 4) {
            return null;
        }
        // 以时间窗口判断是否进行中，不依赖 status=2
        Date now = new Date();
        if (activity.getStartTime() == null || now.before(activity.getStartTime())) return null;
        if (activity.getEndTime() == null || now.after(activity.getEndTime())) return null;
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

        // 插入后重新查询拿到带 ID 的明细列表，确保 ID 不为 null
        List<OshSeckillActivityItem> savedItems = itemMapper.selectItemsByActivityId(activityId);

        // 触发 ES 索引事件（草稿状态，Flink 侧会因 activityStatus!=2 而执行 delete，不写入 ES）
        // 等活动发布后再由 updateActivityStatus 触发 upsert
        // 此处仍发消息，保持链路完整，Flink 会幂等处理
        savedItems.forEach(item -> outboxEventService.saveSeckillItemIndexEvent(
                item.getId(),
                buildUpsertMessage(item, activity, SeckillItemIndexEventType.SECKILL_ITEM_INDEX_CREATE),
                null));

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
            // 查出旧明细 ID，用于发送 DELETE 事件
            List<OshSeckillActivityItem> oldItems = itemMapper.selectItemsByActivityId(dto.getId());

            // 逻辑删除旧明细
            itemMapper.deleteItemsByActivityId(dto.getId());

            // 对旧明细发 DELETE 事件
            oldItems.forEach(oldItem -> outboxEventService.saveSeckillItemIndexDeleteEvent(
                    oldItem.getId(),
                    new SeckillItemIndexDeleteMessage(oldItem.getId()),
                    null));

            // 构建并插入新明细
            List<OshSeckillActivityItem> newItems = buildItemsFromUpdateDTO(dto.getItems(), dto.getId());
            itemMapper.insertItems(newItems);

            // 插入后重新查询拿到带 ID 的明细列表
            List<OshSeckillActivityItem> savedNewItems = itemMapper.selectItemsByActivityId(dto.getId());

            // 对新明细发 UPSERT 事件（草稿状态，Flink 侧不写 ES，等发布后再触发）
            savedNewItems.forEach(item -> outboxEventService.saveSeckillItemIndexEvent(
                    item.getId(),
                    buildUpsertMessage(item, exist, SeckillItemIndexEventType.SECKILL_ITEM_INDEX_UPDATE),
                    null));
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
    @Transactional(rollbackFor = Exception.class)
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
        int result = activityMapper.updateActivityStatusByIds(dto.getIds(), targetStatus);

        // 发布活动时提前预热 Redis 缓存，避免秒杀开始瞬间缓存击穿
        if (targetStatus == 1) {
            dto.getIds().forEach(this::warmUpCache);
        }

        // 下架活动时清理 Redis 缓存，释放内存
        if (targetStatus == 4) {
            dto.getIds().forEach(this::cleanUpCache);
        }

        // 发布（status=1）：发 UPSERT 事件，Flink 侧因 activityStatus=1 不写 ES，等定时任务改为进行中后再触发
        // 进行中（status=2，由定时任务触发）：发 UPSERT 事件，Flink 侧写入 ES
        // 下架（status=4）：发 DELETE 事件，Flink 侧从 ES 删除
        if (targetStatus == 1 || targetStatus == 2) {
            dto.getIds().forEach(activityId -> {
                OshSeckillActivity activity = activityMapper.selectActivityById(activityId);
                if (activity == null) return;
                List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(activityId);
                items.forEach(item -> outboxEventService.saveSeckillItemIndexEvent(
                        item.getId(),
                        buildUpsertMessage(item, activity, SeckillItemIndexEventType.SECKILL_ITEM_INDEX_UPDATE),
                        null));
            });
        }
        if (targetStatus == 4) {
            dto.getIds().forEach(activityId -> {
                List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(activityId);
                items.forEach(item -> outboxEventService.saveSeckillItemIndexDeleteEvent(
                        item.getId(),
                        new SeckillItemIndexDeleteMessage(item.getId()),
                        null));
            });
        }

        return result;
    }

    /**
     * 发布活动后预热 Redis 缓存（提前写入，避免秒杀开始瞬间缓存击穿）
     * 写入 status=1 的活动信息 + 明细信息 + 库存 Key
     * 定时任务只需把活动缓存里的 status 更新为 2，库存 Key 已提前就绪
     */
    private void warmUpCache(Long activityId) {
        OshSeckillActivity activity = activityMapper.selectActivityById(activityId);
        if (activity == null) return;

        Date endTime = activity.getEndTime();

        // 活动/明细信息缓存：活动结束后保留 24 小时
        long activityExpire = SeckillCacheConstants.calcExpireSeconds(endTime, SeckillCacheConstants.ACTIVITY_EXPIRE_BUFFER);
        // 库存 Key：活动结束后保留 2 小时
        long stockExpire = SeckillCacheConstants.calcExpireSeconds(endTime, SeckillCacheConstants.STOCK_EXPIRE_BUFFER);

        // 写入活动缓存
        redisTemplate.opsForValue().set(
                SECKILL_ACTIVITY_KEY + activityId, activity,
                activityExpire, TimeUnit.SECONDS);

        // 写入明细缓存 + 库存 Key
        List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(activityId);
        for (OshSeckillActivityItem item : items) {
            redisTemplate.opsForValue().set(
                    SECKILL_ITEM_KEY + item.getId(), item,
                    activityExpire, TimeUnit.SECONDS);

            String stockKey = SECKILL_STOCK_KEY + activityId + ":" + item.getId();
            // 只在 Key 不存在时写入，避免覆盖已有库存（防止重复发布）
            stringRedisTemplate.opsForValue().setIfAbsent(
                    stockKey, String.valueOf(item.getAvailableStock()),
                    stockExpire, TimeUnit.SECONDS);
        }
        logger.info("【活动发布】活动{}缓存预热完成，明细数量：{}，活动缓存过期：{}s，库存缓存过期：{}s",
                activityId, items.size(), activityExpire, stockExpire);
    }

    /**
     * 活动下架/结束时清理 Redis 缓存，释放内存
     * 清理范围：活动缓存、明细缓存、库存 Key、已购用户 Set
     */
    private void cleanUpCache(Long activityId) {
        // 删除活动缓存
        redisTemplate.delete(SECKILL_ACTIVITY_KEY + activityId);

        // 遍历明细，删除明细缓存、库存 Key、已购 Set
        List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(activityId);
        for (OshSeckillActivityItem item : items) {
            redisTemplate.delete(SECKILL_ITEM_KEY + item.getId());
            stringRedisTemplate.delete(SECKILL_STOCK_KEY + activityId + ":" + item.getId());
            stringRedisTemplate.delete(SECKILL_BOUGHT_KEY + activityId + ":" + item.getId());
        }
        logger.info("【活动下架】活动{}缓存已清理，明细数量：{}", activityId, items.size());
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
        // 逐个删除明细，并发送 DELETE 事件
        ids.forEach(activityId -> {
            List<OshSeckillActivityItem> items = itemMapper.selectItemsByActivityId(activityId);
            itemMapper.deleteItemsByActivityId(activityId);
            items.forEach(item -> outboxEventService.saveSeckillItemIndexDeleteEvent(
                    item.getId(),
                    new SeckillItemIndexDeleteMessage(item.getId()),
                    null));
        });
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

    /**
     * 构建秒杀明细索引 upsert 消息
     */
    private SeckillItemIndexUpsertMessage buildUpsertMessage(
            OshSeckillActivityItem item, OshSeckillActivity activity, String eventType) {
        SeckillItemIndexUpsertMessage msg = new SeckillItemIndexUpsertMessage();
        msg.setEventType(eventType);
        msg.setId(item.getId());
        msg.setActivityId(activity.getId());
        msg.setActivityStatus(activity.getStatus());
        msg.setGoodsId(item.getGoodsId());
        msg.setGoodsType(item.getGoodsType());
        msg.setTitle(item.getTitle());
        msg.setCover(item.getCover());
        msg.setOriginPrice(item.getOriginPrice());
        msg.setSeckillPrice(item.getSeckillPrice());
        msg.setTotalStock(item.getTotalStock());
        msg.setAvailableStock(item.getAvailableStock());
        msg.setSoldCount(item.getSoldCount() != null ? item.getSoldCount() : 0);
        msg.setLimitPerUser(item.getLimitPerUser());
        msg.setSort(item.getSort());
        msg.setActivityTitle(activity.getTitle());
        msg.setPayTimeoutMin(activity.getPayTimeoutMin());
        msg.setStartTime(activity.getStartTime());
        msg.setEndTime(activity.getEndTime());
        msg.setDeleteFlag(item.getDeleteFlag() != null ? item.getDeleteFlag() : 0);
        msg.setCreateTime(item.getCreateTime());
        msg.setUpdateTime(item.getUpdateTime());
        return msg;
    }

    /** 明细实体 → 明细 VO，availableStock 优先从 Redis 实时读取 */
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
        vo.setLimitPerUser(item.getLimitPerUser());
        vo.setSort(item.getSort());
        vo.setSoldCount(item.getSoldCount());

        // 优先从 Redis 读实时库存，Redis 没有则降级用数据库值
        String stockKey = SECKILL_STOCK_KEY + item.getActivityId() + ":" + item.getId();
        String redisStock = stringRedisTemplate.opsForValue().get(stockKey);
        if (redisStock != null) {
            try {
                vo.setAvailableStock(Integer.parseInt(redisStock));
            } catch (NumberFormatException e) {
                vo.setAvailableStock(item.getAvailableStock());
            }
        } else {
            // Redis 没有（活动未开始或缓存过期），降级用数据库值
            vo.setAvailableStock(item.getAvailableStock());
        }

        return vo;
    }
}
