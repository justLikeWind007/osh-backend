package com.backstage.system.controller.common;


import com.backstage.common.annotation.Anonymous;
import com.backstage.common.config.PayConfig;
import com.backstage.system.service.order.impl.PayServiceImpl;
import com.backstage.system.domain.vo.order.PayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayServiceImpl payServiceImpl;

    @Anonymous
    // 前端下单 得到 二维码
    @PostMapping("/create")
    public PayResponse create(
            @RequestParam String name,
            @RequestParam String money,
            HttpServletRequest request
    ) {
        // 自动生成订单号
        String outTradeNo = UUID.randomUUID().toString().replace("-", "");
        // 客户的ip
        String ip = getClientIp(request);

        // 创建订单
        PayResponse response = payServiceImpl.createPay(outTradeNo, name, money, ip);

        response.setOut_trade_no(outTradeNo);

        return response;
    }

    // 查询订单状态（前端用于轮询）
    @Anonymous
    @GetMapping("/status")
    public Map<String, Object> status(@RequestParam String out_trade_no) {

        // [API]查询单个订单
        String queryUrl = PayConfig.STATUS
                + "?act=order"
                + "&pid=" + PayConfig.PID
                + "&key=" + PayConfig.KEY
                + "&out_trade_no=" + out_trade_no;

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> res = restTemplate.getForObject(queryUrl, Map.class);

        boolean payStatus = false;

        // code 1为成功查询到了，status 1 为成功已经支付 , 0为未支付
        if (res != null) {
            Object code = res.get("code");
            if (code != null && "1".equals(code.toString())) {
                Object status = res.get("status");
                if (status != null && "1".equals(status.toString())) {
                    payStatus = true;
                }
            }
        }

        Map<String, Object> map = new HashMap<>();
        map.put("payStatus", payStatus);
        return map;
    }

    // 获取客户端ip
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty())
            ip = request.getRemoteAddr();
        return ip;
    }
}
