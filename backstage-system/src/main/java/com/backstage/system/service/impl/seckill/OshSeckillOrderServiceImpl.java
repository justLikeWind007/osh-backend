package com.backstage.system.service.impl.seckill;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.config.PayConfig;
import com.backstage.common.constant.KafkaConstants;
import com.backstage.common.constant.SeckillCacheConstants;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.utils.kafka.KafkaMessageUtil;
import com.backstage.system.domain.seckill.OshSeckillActivity;
import com.backstage.system.domain.seckill.OshSeckillActivityItem;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.domain.vo.seckill.SeckillOrderAdminVO;
import com.backstage.system.domain.vo.seckill.SeckillResultVO;
import com.backstage.system.mapper.seckill.OshSeckillActivityItemMapper;
import com.backstage.system.mapper.seckill.OshSeckillActivityMapper;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import com.backstage.system.service.seckill.IOshSeckillOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 秒杀订单 Service 实现
 *
 * @author backstage
 * @date 2026-04-28
 */
@Service
public class OshSeckillOrderServiceImpl implements IOshSeckillOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OshSeckillOrderServiceImpl.class);

    /**
     * Redis Key 前缀（统一引用 SeckillCacheConstants，避免多处定义不一致）
     *   seckill:stock:{activityId}:{itemId}              库存计数器
     *   seckill:bought:{activityId}:{itemId}             已购用户 Set
     *   seckill:activity:{activityId}                    活动基本信息缓存
     *   seckill:item:{itemId}                            明细信息缓存
     *   seckill:order:{activityId}:{itemId}:{userId}     用户秒杀单号（流程状态标记）
     */
    private static final String SECKILL_STOCK_KEY    = SeckillCacheConstants.SECKILL_STOCK_KEY;
    private static final String SECKILL_BOUGHT_KEY   = SeckillCacheConstants.SECKILL_BOUGHT_KEY;
    private static final String SECKILL_BOUGHT_CNT_KEY = SeckillCacheConstants.SECKILL_BOUGHT_CNT_KEY;
    private static final String SECKILL_ACTIVITY_KEY = SeckillCacheConstants.SECKILL_ACTIVITY_KEY;
    private static final String SECKILL_ITEM_KEY     = SeckillCacheConstants.SECKILL_ITEM_KEY;
    private static final String SECKILL_ORDER_KEY    = SeckillCacheConstants.SECKILL_ORDER_KEY;

    /**
     * Lua 脚本：原子扣减库存 + 限购校验 + 记录已购数量
     * KEYS[1] = 库存 key（seckill:stock:{activityId}:{itemId}）
     * KEYS[2] = 用户已购数量 key（seckill:bought_cnt:{activityId}:{itemId}:{userId}）
     * ARGV[1] = 本次购买数量（quantity）
     * ARGV[2] = 每人限购上限（limitPerUser）
     * ARGV[3] = 已购数量 Key 过期时间（秒）
     * 返回值：
     *   1  = 秒杀成功
     *  -1  = 库存不足
     *  -2  = 超过限购数量
     */
    private static final String SECKILL_LUA_SCRIPT =
            "local stock = tonumber(redis.call('get', KEYS[1])) \n" +
            "local qty   = tonumber(ARGV[1]) \n" +
            "local limit = tonumber(ARGV[2]) \n" +
            "if stock == nil or stock < qty then \n" +
            "    return -1 \n" +
            "end \n" +
            "local bought = tonumber(redis.call('get', KEYS[2])) or 0 \n" +
            "if bought + qty > limit then \n" +
            "    return -2 \n" +
            "end \n" +
            "redis.call('decrby', KEYS[1], qty) \n" +
            "local newBought = redis.call('incrby', KEYS[2], qty) \n" +
            "if newBought == qty then \n" +
            "    redis.call('expire', KEYS[2], tonumber(ARGV[3])) \n" +
            "end \n" +
            "return 1";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /** 专门用于库存 Key 的读写，保证存的是纯数字字符串，Lua 脚本 tonumber() 才能正确解析 */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OshSeckillActivityMapper activityMapper;

    @Autowired
    private OshSeckillActivityItemMapper itemMapper;

    @Autowired
    private OshSeckillOrderMapper orderMapper;

    @Resource
    private PayConfig payConfig;

    /**
     * 接口7：管理端查询秒杀订单列表
     */
    @Override
    public List<SeckillOrderAdminVO> selectOrderList(OshSeckillOrder order) {
        return orderMapper.selectOrderList(order)
                .stream()
                .map(this::toAdminVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据秒杀单号查询订单
     */
    @Override
    public OshSeckillOrder getOrderBySeckillNo(String seckillNo, Long userId) {
        return orderMapper.selectOrderBySeckillNo(seckillNo);
    }

    /**
     * 通过秒杀单号查询订单状态（支付完成后前端轮询用）
     * 校验订单归属，防止越权查询
     * 若订单 status=0，主动调易支付确认是否已付款，避免依赖回调
     */
    @Override
    public SeckillResultVO getOrderStatusBySeckillNo(String seckillNo, Long userId) {
        OshSeckillOrder order = orderMapper.selectOrderBySeckillNo(seckillNo);
        if (order == null || !order.getUserId().equals(userId)) {
            return null;
        }
        // status=0（待支付）时，主动查询易支付确认是否已付款
        if (order.getStatus() == 0) {
            try {
                String queryUrl = payConfig.STATUS_URL
                        + "?act=order"
                        + "&pid=" + payConfig.PID
                        + "&key=" + payConfig.KEY
                        + "&out_trade_no=" + seckillNo;
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                java.util.Map<?, ?> res = restTemplate.getForObject(queryUrl, java.util.Map.class);
                if (res != null
                        && "1".equals(String.valueOf(res.get("code")))
                        && "1".equals(String.valueOf(res.get("status")))) {
                    // 易支付确认已支付，更新订单状态
                    OshSeckillOrder update = new OshSeckillOrder();
                    update.setId(order.getId());
                    update.setStatus(1);
                    update.setPayTime(new Date());
                    orderMapper.updateOrder(update);
                    // 写入已购数量 Key（兜底，防止 Key 过期后用户重复购买超限）
                    String boughtCntKey = SECKILL_BOUGHT_CNT_KEY + order.getActivityId() + ":" + order.getItemId() + ":" + userId;
                    String existingCnt = stringRedisTemplate.opsForValue().get(boughtCntKey);
                    if (existingCnt == null) {
                        int qty = order.getQuantity() != null ? order.getQuantity() : 1;
                        OshSeckillActivity act = getActivityFromCache(order.getActivityId());
                        long expire = SeckillCacheConstants.calcExpireSeconds(
                                act != null ? act.getEndTime() : null,
                                SeckillCacheConstants.BOUGHT_CNT_EXPIRE_BUFFER);
                        stringRedisTemplate.opsForValue().set(boughtCntKey, String.valueOf(qty), expire, TimeUnit.SECONDS);
                    }
                    logger.info("【秒杀状态查询】主动确认支付成功，seckillNo={}", seckillNo);
                    order.setStatus(1);
                    order.setPayTime(new Date());
                }
            } catch (Exception e) {
                logger.warn("【秒杀状态查询】主动查询支付状态失败，seckillNo={}, error={}", seckillNo, e.getMessage());
            }
        }
        return toResultVO(order);
    }

    /**
     * 接口10：执行秒杀
     * 用户需指定 activityId + itemId，表示抢这场活动里的哪个商品
     */
    @Override
    public SeckillResultVO doSeckill(Long activityId, Long itemId, Long userId, int quantity) {

        // 1. 校验活动状态
        OshSeckillActivity activity = getActivityFromCache(activityId);
        if (activity == null) {
            throw new ServiceException("活动不存在");
        }
        // 已下架/已删除直接拦截
        if (activity.getStatus() == 4) {
            throw new ServiceException("活动已下架");
        }
        // 以时间窗口作为"是否进行中"的唯一标准，不依赖定时任务更新的 status 字段
        Date now = new Date();
        if (activity.getStartTime() == null || now.before(activity.getStartTime())) {
            throw new ServiceException("活动尚未开始");
        }
        if (activity.getEndTime() == null || now.after(activity.getEndTime())) {
            throw new ServiceException("活动已结束");
        }

        // 2. 校验明细（商品）是否属于该活动
        OshSeckillActivityItem item = getItemFromCache(itemId);
        if (item == null || !item.getActivityId().equals(activityId)) {
            throw new ServiceException("商品不存在或不属于该活动");
        }

        // 3. 校验购买数量
        int limitPerUser = item.getLimitPerUser() != null ? item.getLimitPerUser() : 1;
        if (quantity < 1) {
            throw new ServiceException("购买数量不能小于1");
        }
        if (quantity > limitPerUser) {
            throw new ServiceException("超过限购数量，每人最多购买" + limitPerUser + "件");
        }

        // 4. 执行 Lua 脚本（原子扣减库存 + 限购校验 + 累加已购数量）
        String stockKey    = SECKILL_STOCK_KEY     + activityId + ":" + itemId;
        String boughtCntKey = SECKILL_BOUGHT_CNT_KEY + activityId + ":" + itemId + ":" + userId;
        String orderKey    = SECKILL_ORDER_KEY     + activityId + ":" + itemId + ":" + userId;

        // 4.1 先检查流程状态 Key，防止用户重复点击
        Object existingNo = redisTemplate.opsForValue().get(orderKey);
        if (existingNo != null) {
            throw new ServiceException("您已在秒杀流程中，请勿重复提交，单号：" + existingNo);
        }

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(SECKILL_LUA_SCRIPT);
        script.setResultType(Long.class);

        Long result = stringRedisTemplate.execute(
                script,
                Arrays.asList(stockKey, boughtCntKey),
                String.valueOf(quantity),
                String.valueOf(limitPerUser),
                String.valueOf(SeckillCacheConstants.calcExpireSeconds(activity.getEndTime(), SeckillCacheConstants.BOUGHT_CNT_EXPIRE_BUFFER))
        );

        if (result == null || result == -1L) {
            throw new ServiceException("手慢了，库存不足");
        }
        if (result == -2L) {
            throw new ServiceException("超过限购数量，每人最多购买" + limitPerUser + "件");
        }

        // 5. 生成秒杀订单号
        String seckillNo = generateSeckillNo();

        // 6. 计算支付截止时间
        Date payExpireTime = calcPayExpireTime(activity.getPayTimeoutMin());

        // 7. 写入流程状态 Key
        long expireSeconds = (activity.getPayTimeoutMin() != null ? activity.getPayTimeoutMin() : 15) * 60L;
        redisTemplate.opsForValue().set(orderKey, seckillNo, expireSeconds, TimeUnit.SECONDS);

        // =====================================================================
        // 【临时方案 - 测试用】直接同步写库，绕过 Kafka
        // 待 Kafka 依赖冲突（redisson-spring-data 版本问题）修复后，
        // 删除下方临时代码，恢复下面注释掉的 Kafka 方案
        // =====================================================================
        try {
            // 扣减数据库库存
            int affected = itemMapper.decrStock(itemId, quantity);
            if (affected == 0) {
                // 数据库库存不足，回滚 Redis
                stringRedisTemplate.opsForValue().increment(stockKey, quantity);
                stringRedisTemplate.opsForValue().increment(boughtCntKey, -quantity);
                redisTemplate.delete(orderKey);
                throw new ServiceException("库存不足");
            }
            // 直接写入订单表
            OshSeckillOrder order = new OshSeckillOrder();
            order.setSeckillNo(seckillNo);
            order.setActivityId(activityId);
            order.setItemId(itemId);
            order.setUserId(userId);
            order.setGoodsId(item.getGoodsId());
            order.setGoodsType(item.getGoodsType());
            order.setGoodsTitle(item.getTitle());
            order.setGoodsCover(item.getCover());
            order.setOriginPrice(item.getOriginPrice());
            order.setSeckillPrice(item.getSeckillPrice());
            order.setQuantity(quantity);
            order.setStatus(0); // 待支付
            order.setPayExpireTime(payExpireTime);
            orderMapper.insertOrder(order);
            logger.info("【秒杀-临时】订单同步写库成功，seckillNo={}, userId={}, quantity={}", seckillNo, userId, quantity);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("【秒杀-临时】订单写库失败，回滚 Redis，seckillNo={}", seckillNo, e);
            stringRedisTemplate.opsForValue().increment(stockKey, quantity);
            stringRedisTemplate.opsForValue().increment(boughtCntKey, -quantity);
            redisTemplate.delete(orderKey);
            throw new ServiceException("秒杀失败，请重试");
        }
        // =====================================================================
        // 【Kafka 方案 - 待恢复】修复 redisson-spring-data 依赖冲突后，
        // 删除上方临时代码，恢复以下注释
        // =====================================================================
//        SeckillOrderMessage message = new SeckillOrderMessage();
//        message.setSeckillNo(seckillNo);
//        message.setActivityId(activityId);
//        message.setItemId(itemId);
//        message.setUserId(userId);
//        message.setGoodsId(item.getGoodsId());
//        message.setGoodsType(item.getGoodsType());
//        message.setGoodsTitle(item.getTitle());
//        message.setGoodsCover(item.getCover());
//        message.setOriginPrice(item.getOriginPrice());
//        message.setSeckillPrice(item.getSeckillPrice());
//        message.setQuantity(quantity);
//        message.setPayExpireTime(payExpireTime);
//        try {
//            KafkaMessageUtil.sendMessage(
//                    KafkaConstants.SECKILL_ORDER_CREATE_TOPIC,
//                    seckillNo,
//                    JSON.toJSONString(message)
//            );
//            logger.info("【秒杀】发送订单创建消息成功，seckillNo={}, userId={}, activityId={}, itemId={}",
//                    seckillNo, userId, activityId, itemId);
//        } catch (Exception e) {
//            logger.error("【秒杀】Kafka 消息发送失败，回滚 Redis，seckillNo={}", seckillNo, e);
//            stringRedisTemplate.opsForValue().increment(stockKey, quantity);
//            stringRedisTemplate.opsForValue().increment(boughtCntKey, -quantity);
//            redisTemplate.delete(orderKey);
//            throw new ServiceException("秒杀失败，请重试");
//        }

        // 8. 立即返回结果给前端
        SeckillResultVO vo = new SeckillResultVO();
        vo.setSeckillNo(seckillNo);
        vo.setStatus(0); // 待支付
        vo.setGoodsId(item.getGoodsId());
        vo.setGoodsType(item.getGoodsType());
        vo.setGoodsTitle(item.getTitle());
        vo.setGoodsCover(item.getCover());
        vo.setOriginPrice(item.getOriginPrice());
        vo.setSeckillPrice(item.getSeckillPrice());
        vo.setQuantity(quantity);
        vo.setPayExpireTime(payExpireTime);
        return vo;
    }

    /**
     * 接口11：查询秒杀结果（前端轮询）
     * 优先从 Redis 流程状态 Key 拿单号，再查订单详情
     * 好处：Kafka 消费有延迟时，Redis 里已有单号，前端能更快得到反馈
     */
    @Override
    public SeckillResultVO getSeckillResult(Long activityId, Long itemId, Long userId) {
        // 先查 Redis 流程状态 Key
        String orderKey = SECKILL_ORDER_KEY + activityId + ":" + itemId + ":" + userId;
        Object cachedNo = redisTemplate.opsForValue().get(orderKey);
        if (cachedNo == null) {
            // Key 不存在：用户未参与或订单已完结（超时/取消后 Key 自动过期）
            return null;
        }
        // Key 存在，用单号查订单详情（Kafka 可能还没消费完，order 可能为 null）
        String seckillNo = cachedNo.toString();
        OshSeckillOrder order = orderMapper.selectOrderBySeckillNo(seckillNo);
        if (order == null) {
            // 订单还未落库（Kafka 消费中），返回一个"处理中"的占位结果
            SeckillResultVO pending = new SeckillResultVO();
            pending.setSeckillNo(seckillNo);
            pending.setStatus(-1); // -1 表示处理中，前端继续轮询
            return pending;
        }
        return toResultVO(order);
    }

    /**
     * 接口12：取消秒杀订单
     */
    @Override
    public void cancelOrder(String seckillNo, Long userId) {
        OshSeckillOrder order = orderMapper.selectOrderBySeckillNo(seckillNo);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new ServiceException("只有待支付的订单才能取消");
        }

        // 更新订单状态为已取消
        OshSeckillOrder update = new OshSeckillOrder();
        update.setId(order.getId());
        update.setStatus(2);
        update.setCancelTime(new Date());
        update.setCancelReason("user_cancel");
        orderMapper.updateOrder(update);

        // 回滚 Redis 库存、已购数量、流程状态 Key
        String stockKey    = SECKILL_STOCK_KEY     + order.getActivityId() + ":" + order.getItemId();
        String boughtCntKey = SECKILL_BOUGHT_CNT_KEY + order.getActivityId() + ":" + order.getItemId() + ":" + userId;
        String orderKey    = SECKILL_ORDER_KEY     + order.getActivityId() + ":" + order.getItemId() + ":" + userId;
        int qty = order.getQuantity() != null ? order.getQuantity() : 1;
        stringRedisTemplate.opsForValue().increment(stockKey, qty);
        stringRedisTemplate.opsForValue().increment(boughtCntKey, -qty);
        redisTemplate.delete(orderKey);

        logger.info("【秒杀】用户取消订单，seckillNo={}, userId={}", seckillNo, userId);
    }

    // ==================== 私有方法 ====================

    /**
     * 从 Redis 缓存获取活动信息，没有则查 DB 并写入缓存
     */
    private OshSeckillActivity getActivityFromCache(Long activityId) {
        String cacheKey = SECKILL_ACTIVITY_KEY + activityId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return JSON.parseObject(JSON.toJSONString(cached), OshSeckillActivity.class);
        }
        OshSeckillActivity activity = activityMapper.selectActivityById(activityId);
        if (activity != null) {
            long expire = SeckillCacheConstants.calcExpireSeconds(activity.getEndTime(), SeckillCacheConstants.ACTIVITY_EXPIRE_BUFFER);
            redisTemplate.opsForValue().set(cacheKey, activity, expire, TimeUnit.SECONDS);
        }
        return activity;
    }

    /**
     * 从 Redis 缓存获取明细信息，没有则查 DB 并写入缓存
     * 同时初始化该明细的库存 Key（如果还没有）
     *
     * 注意：库存 Key 过期重建时使用数据库的 available_stock（实时剩余库存），
     * 而非 total_stock（原始总库存），避免库存"复活"导致超卖。
     */
    private OshSeckillActivityItem getItemFromCache(Long itemId) {
        String cacheKey = SECKILL_ITEM_KEY + itemId;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        OshSeckillActivityItem item;
        if (cached != null) {
            item = JSON.parseObject(JSON.toJSONString(cached), OshSeckillActivityItem.class);
        } else {
            item = itemMapper.selectItemById(itemId);
            if (item != null) {
                // 需要活动结束时间来计算过期时间，从活动缓存里取
                OshSeckillActivity activity = getActivityFromCache(item.getActivityId());
                Date endTime = activity != null ? activity.getEndTime() : null;
                long expire = SeckillCacheConstants.calcExpireSeconds(endTime, SeckillCacheConstants.ACTIVITY_EXPIRE_BUFFER);
                redisTemplate.opsForValue().set(cacheKey, item, expire, TimeUnit.SECONDS);
            }
        }
        // 库存 Key 不存在时重建（首次初始化 或 Key 过期后懒加载重建）
        if (item != null) {
            String stockKey = SECKILL_STOCK_KEY + item.getActivityId() + ":" + itemId;
            if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(stockKey))) {
                OshSeckillActivityItem freshItem = itemMapper.selectItemById(itemId);
                if (freshItem != null) {
                    OshSeckillActivity activity = getActivityFromCache(item.getActivityId());
                    Date endTime = activity != null ? activity.getEndTime() : null;
                    long stockExpire = SeckillCacheConstants.calcExpireSeconds(endTime, SeckillCacheConstants.STOCK_EXPIRE_BUFFER);
                    stringRedisTemplate.opsForValue().set(stockKey,
                            String.valueOf(freshItem.getAvailableStock()),
                            stockExpire, TimeUnit.SECONDS);
                    logger.info("【库存重建】itemId={}, 实时剩余库存={}, 过期时间={}s", itemId, freshItem.getAvailableStock(), stockExpire);
                }
            }
        }
        return item;
    }

    /**
     * 生成秒杀订单号：SK + yyyyMMddHHmmss + 3位随机数
     */
    private String generateSeckillNo() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        int random = (int) (Math.random() * 900) + 100;
        return "SK" + timestamp + random;
    }

    /**
     * 计算支付截止时间
     */
    private Date calcPayExpireTime(Integer payTimeoutMin) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, payTimeoutMin != null ? payTimeoutMin : 15);
        return cal.getTime();
    }

    /** 订单实体转管理端 VO */
    private SeckillOrderAdminVO toAdminVO(OshSeckillOrder order) {
        SeckillOrderAdminVO vo = new SeckillOrderAdminVO();
        vo.setId(order.getId());
        vo.setSeckillNo(order.getSeckillNo());
        vo.setActivityId(order.getActivityId());
        vo.setUserId(order.getUserId());
        vo.setGoodsId(order.getGoodsId());
        vo.setGoodsType(order.getGoodsType());
        vo.setGoodsTitle(order.getGoodsTitle());
        vo.setOriginPrice(order.getOriginPrice());
        vo.setSeckillPrice(order.getSeckillPrice());
        vo.setQuantity(order.getQuantity());
        vo.setStatus(order.getStatus());
        vo.setPayTime(order.getPayTime());
        vo.setPayExpireTime(order.getPayExpireTime());
        vo.setCancelTime(order.getCancelTime());
        vo.setCancelReason(order.getCancelReason());
        vo.setOshOrderNo(order.getOshOrderNo());
        vo.setCreateTime(order.getCreateTime());
        return vo;
    }

    /** 订单实体转结果 VO */
    private SeckillResultVO toResultVO(OshSeckillOrder order) {
        SeckillResultVO vo = new SeckillResultVO();
        vo.setSeckillNo(order.getSeckillNo());
        vo.setStatus(order.getStatus());
        vo.setGoodsId(order.getGoodsId());
        vo.setGoodsType(order.getGoodsType());
        vo.setGoodsTitle(order.getGoodsTitle());
        vo.setGoodsCover(order.getGoodsCover());
        vo.setOriginPrice(order.getOriginPrice());
        vo.setSeckillPrice(order.getSeckillPrice());
        vo.setQuantity(order.getQuantity());
        vo.setPayExpireTime(order.getPayExpireTime());
        return vo;
    }

    // ==================== 内部消息类 ====================

    /**
     * Kafka 消息体（秒杀订单创建消息）
     */
    public static class SeckillOrderMessage {
        private String seckillNo;
        private Long activityId;
        private Long itemId;
        private Long userId;
        private Long goodsId;
        private Integer goodsType;
        private String goodsTitle;
        private String goodsCover;
        private java.math.BigDecimal originPrice;
        private java.math.BigDecimal seckillPrice;
        private Integer quantity;
        private Date payExpireTime;

        public String getSeckillNo() { return seckillNo; }
        public void setSeckillNo(String seckillNo) { this.seckillNo = seckillNo; }
        public Long getActivityId() { return activityId; }
        public void setActivityId(Long activityId) { this.activityId = activityId; }
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getGoodsId() { return goodsId; }
        public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
        public Integer getGoodsType() { return goodsType; }
        public void setGoodsType(Integer goodsType) { this.goodsType = goodsType; }
        public String getGoodsTitle() { return goodsTitle; }
        public void setGoodsTitle(String goodsTitle) { this.goodsTitle = goodsTitle; }
        public String getGoodsCover() { return goodsCover; }
        public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }
        public java.math.BigDecimal getOriginPrice() { return originPrice; }
        public void setOriginPrice(java.math.BigDecimal originPrice) { this.originPrice = originPrice; }
        public java.math.BigDecimal getSeckillPrice() { return seckillPrice; }
        public void setSeckillPrice(java.math.BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Date getPayExpireTime() { return payExpireTime; }
        public void setPayExpireTime(Date payExpireTime) { this.payExpireTime = payExpireTime; }
    }
}
