package com.backstage.system.controller.order;


import com.backstage.system.service.order.PayService;
import com.backstage.system.domain.vo.order.PayResponse;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/pay")
public class PayController {

    private final PayService payService;

    public PayController(PayService payService) {
        this.payService = payService;
    }

    // 前端下单 → 获取微信二维码
    @PostMapping("/create")
    public PayResponse create(
            @RequestParam String name,
            @RequestParam String money,
            HttpServletRequest request
    ) {
        // 自动生成订单号
        String outTradeNo = UUID.randomUUID().toString().replace("-", "");
        String ip = getClientIp(request);

        return payService.createPay(outTradeNo, name, money, ip);
    }

    // 查询订单状态（前端轮询）
    @GetMapping("/status")
    public Map<String, Object> status(@RequestParam String out_trade_no) {
        Map<String, Object> map = new java.util.HashMap<>();
        // 自己写的数据库查询是否支付
        map.put("payStatus", false);
        return map;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }
}
