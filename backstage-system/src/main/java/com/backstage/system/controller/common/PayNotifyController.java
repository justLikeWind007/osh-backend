package com.backstage.system.controller.common;

import com.backstage.common.annotation.Anonymous;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import com.backstage.system.service.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notify")
public class PayNotifyController {

    private static final Logger log = LoggerFactory.getLogger(PayNotifyController.class);

    @Resource
    private OrderService orderService;

    @Anonymous
    @GetMapping("/pay")
    public String notify(HttpServletRequest request) {
        try {
            // 接收回调参数
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> req = request.getParameterMap();
            for (String key : req.keySet()) {
                params.put(key, req.get(key)[0]);
            }
            log.info("【支付模块】支付回调，参数如下：{}",params);


            String outTradeNo = params.get("out_trade_no");
            log.info("【支付回调】收到支付成功通知，outTradeNo={}", outTradeNo);
            // 4. 判断是秒杀订单（SK 开头）
            if (outTradeNo != null && outTradeNo.startsWith("SK")) {
                handleSeckillOrderPaid(outTradeNo);
                return "success";
            }
            return orderService.handlePayNotify(params) ? "success" : "FAIL";
        } catch (Exception e) {
            return "FAIL";
        }
    }


    private static final String SECKILL_BOUGHT_KEY = "seckill:bought:";

    @Autowired
    private OshSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 处理秒杀订单支付成功
     */
    private void handleSeckillOrderPaid(String seckillNo) {
        // 幂等校验：已支付则跳过
        OshSeckillOrder order = seckillOrderMapper.selectOrderBySeckillNo(seckillNo);
        if (order == null) {
            log.warn("【支付回调】秒杀订单不存在，seckillNo={}", seckillNo);
            return;
        }
        if (order.getStatus() == 1) {
            log.info("【支付回调】订单已支付，跳过重复处理，seckillNo={}", seckillNo);
            return;
        }
        if (order.getStatus() != 0) {
            log.warn("【支付回调】订单状态异常（非待支付），status={}, seckillNo={}", order.getStatus(), seckillNo);
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

        log.info("【支付回调】秒杀订单支付成功，seckillNo={}", seckillNo);
    }
}
