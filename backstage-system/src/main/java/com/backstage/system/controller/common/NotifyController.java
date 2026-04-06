package com.backstage.system.controller.common;

import com.backstage.common.annotation.Anonymous;
import com.backstage.system.utils.SignUtil;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notify")
public class NotifyController {
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

            // 验签 防止伪造
            String sign = params.get("sign");
            String localSign = SignUtil.createSign(params);

            if (!sign.equals(localSign)) {
                return "FAIL";
            }

            // 支付成功
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                String outTradeNo = params.get("out_trade_no");
                System.out.println("订单号：" + outTradeNo);
                // 更新数据库订单状态
                

                return "success";
            }
            return "FAIL";
        } catch (Exception e) {
            return "FAIL";
        }
    }
}