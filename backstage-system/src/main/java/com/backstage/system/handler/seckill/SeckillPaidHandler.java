package com.backstage.system.handler.seckill;

import com.backstage.system.domain.order.enums.ProductTypeEnum;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.announcement.ISeckillAnnouncementService;
import com.backstage.system.service.order.OrderPaidHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SeckillPaidHandler implements OrderPaidHandler {

    private static final Logger logger = LoggerFactory.getLogger(SeckillPaidHandler.class);

    private static final String SECKILL_BOUGHT_KEY = "seckill:bought:";

    @Autowired
    private OshSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private OshUserMapper userMapper;

    @Autowired
    private ISeckillAnnouncementService seckillAnnouncementService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取业务类型标识。
     *
     * @return 业务类型标识
     */
    @Override
    public String bizType() {
        return ProductTypeEnum.SECKILL.getName();
    }

    /**
     * 处理秒杀订单支付成功
     */
    @Override
    public void handle(String seckillNo) {
        // 幂等校验：已支付则跳过
        OshSeckillOrder order = seckillOrderMapper.selectOrderBySeckillNo(seckillNo);
        if (order == null) {
            logger.warn("【支付回调】秒杀订单不存在，seckillNo={}", seckillNo);
            return;
        }
        if (order.getStatus() == 1) {
            logger.info("【支付回调】订单已支付，跳过重复处理，seckillNo={}", seckillNo);
            return;
        }
        if (order.getStatus() != 0) {
            logger.warn("【支付回调】订单状态异常（非待支付），status={}, seckillNo={}", order.getStatus(), seckillNo);
            return;
        }

        // 更新订单状态为已支付
        OshSeckillOrder update = new OshSeckillOrder();
        update.setId(order.getId());
        update.setStatus(1);
        update.setPayTime(new Date());
        seckillOrderMapper.updateOrder(update);

        // 支付成功后写入已购 Set，永久拦截该用户重复购买同一商品
        String boughtKey = SECKILL_BOUGHT_KEY + order.getActivityId() + ":" + order.getItemId();
        stringRedisTemplate.opsForSet().add(boughtKey, String.valueOf(order.getUserId()));

        // 支付成功后写入秒杀动态到 osh_announcement
        try {
            String maskedUsername = buildMaskedUsername(order.getUserId());
            seckillAnnouncementService.insertSeckillDynamic(
                    maskedUsername, order.getGoodsTitle(), order.getGoodsId());
        } catch (Exception e) {
            // 动态写入失败不影响主流程
            logger.error("【支付回调】写入秒杀动态失败，seckillNo={}, error={}", seckillNo, e.getMessage());
        }

        logger.info("【支付回调】秒杀订单支付成功，seckillNo={}", seckillNo);
    }

    /**
     * 根据 userId 查询用户，构建脱敏用户名
     * 昵称优先，昵称为空则用登录名，取前2位 + **
     */
    private String buildMaskedUsername(Long userId) {
        if (userId == null) {
            return "某用户";
        }
        try {
            OshUser user = userMapper.selectUserById(userId);
            if (user == null) {
                return "某用户";
            }
            String name = user.getUsername();
            if (name == null || name.isEmpty()) {
                return "某用户";
            }
            int len = name.length();
            return len <= 2 ? name.charAt(0) + "**" : name.substring(0, 2) + "**";
        } catch (Exception e) {
            logger.warn("【支付回调】获取用户信息失败，userId={}", userId);
            return "某用户";
        }
    }
}
