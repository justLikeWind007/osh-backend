package com.backstage.system.service.order.impl;

import com.backstage.common.config.PayConfig;
import com.backstage.system.service.order.PayService;
import com.backstage.system.utils.SignUtil;
import com.backstage.system.domain.vo.order.PayResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    public PayResponse createPay(String outTradeNo, String name, String money, String clientIp) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("pid", PayConfig.PID);
            params.put("type", "wxpay");
            params.put("out_trade_no", outTradeNo);
            params.put("notify_url", PayConfig.NOTIFY_URL);
            params.put("return_url", PayConfig.RETURN_URL);
            params.put("name", name);
            params.put("money", money);
            params.put("clientip", clientIp);
            params.put("device", "pc");
            params.put("sign_type", "MD5");

            // 生成签名 MD5
            String sign = SignUtil.createSign(params);
            params.put("sign", sign);

            // 请求易支付 mapi.php
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
            request.setAll(params);

            // 地址 表单参数 返回类型字符串
            String result = restTemplate.postForObject(PayConfig.API_URL, request, String.class);

            // 把JSON字符串 转成 PayResponse对象 并 返回
            return new ObjectMapper().readValue(result, PayResponse.class);

        } catch (Exception e) {
            PayResponse resp = new PayResponse();
            resp.setCode(0);
            resp.setMsg("请求失败");
            return resp;
        }
    }
}
