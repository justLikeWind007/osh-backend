package com.backstage.system.controller.common;

import com.backstage.common.annotation.Anonymous;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.mapper.seckill.OshSeckillOrderMapper;
import com.backstage.system.utils.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notify")
public class NotifyController {

    private static final Logger logger = LoggerFactory.getLogger(NotifyController.class);

    @Autowired
    private OshSeckillOrderMapper seckillOrderMapper;

    @Anonymous
    @GetMapping("/pay")
    public String notify(HttpServletRequest request) {
        try {
            // 1. 接收回调参数
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> req = request.getParameterMap();
            for (String key : req.keySet()) {
                params.put(key, req.get(key)[0]);
            }

            // 2. 验签，防止伪造
            String sign = params.get("sign");
            String localSign = SignUtil.createSign(params);
            if (!sign.equals(localSign)) {
                logger.warn("【支付回调】验签失败，params={}", params);
                return "FAIL";
            }

            // 3. 只处理支付成功的回调
            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus)) {
                return "FAIL";
            }

            String outTradeNo = params.get("out_trade_no");
            logger.info("【支付回调】收到支付成功通知，outTradeNo={}", outTradeNo);

            // 4. 判断是秒杀订单（SK 开头）
            if (outTradeNo != null && outTradeNo.startsWith("SK")) {
                handleSeckillOrderPaid(outTradeNo);
            }

            return "success";

        } catch (Exception e) {
            logger.error("【支付回调】处理异常", e);
            return "FAIL";
        }
    }

    /**
     * 处理秒杀订单支付成功
     */
    private void handleSeckillOrderPaid(String seckillNo) {
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

        logger.info("【支付回调】秒杀订单支付成功，seckillNo={}", seckillNo);
    }
}
