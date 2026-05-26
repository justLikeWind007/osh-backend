package com.backstage.system.controller.pay;

import com.backstage.common.annotation.Anonymous;
import com.backstage.system.service.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> req = request.getParameterMap();
            for (String key : req.keySet()) {
                params.put(key, req.get(key)[0]);
            }
            log.info("【支付模块】支付回调，参数如下：{}", params);
            return orderService.handlePayNotify(params) ? "success" : "FAIL";
        } catch (Exception e) {
            return "FAIL";
        }
    }
}
