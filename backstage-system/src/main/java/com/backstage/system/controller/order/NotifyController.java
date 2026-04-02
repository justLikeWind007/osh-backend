package com.backstage.system.controller.order;

import com.backstage.system.utils.SignUtil;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/notify")
public class NotifyController {

    @PostMapping("/pay")
    public String notify(HttpServletRequest request) {
        try {
            // 接收回调参数
            Map<String, String> params = new java.util.HashMap<>();
            Map<String, String[]> req = request.getParameterMap();
            for (String key : req.keySet()) {
                params.put(key, req.get(key)[0]);
            }

            // 验签
            String sign = params.get("sign");
            String localSign = SignUtil.createSign(params);

            if (!sign.equals(localSign)) {
                return "FAIL";
            }

            // 支付成功
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus)) {
                String outTradeNo = params.get("out_trade_no");
                // 更新数据库订单状态
                return "success";
            }
            return "FAIL";
        } catch (Exception e) {
            return "FAIL";
        }
    }
}