package com.backstage.system.service.order.impl;

import com.backstage.common.config.PayConfig;
import com.backstage.system.service.order.PayService;
import com.backstage.system.utils.SignUtil;
import com.backstage.system.domain.vo.order.PayResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Service
public class PayServiceImpl implements PayService {

    private static final Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private PayConfig payConfig;

    @Override
    public PayResponse createPay(String outTradeNo, String name, String money, String clientIp, String channel) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("pid", payConfig.PID);
            params.put("type", channel);
            params.put("out_trade_no", outTradeNo);
            params.put("notify_url", PayConfig.NOTIFY_URL);
            params.put("return_url", PayConfig.RETURN_URL);
            params.put("name", name);
            params.put("money", money);
            params.put("clientip", clientIp);
            params.put("device", "pc");
            params.put("sign_type", "MD5");

            // 生成签名 MD5
            String sign = SignUtil.createSign(params,payConfig.KEY);
            params.put("sign", sign);

            // 请求易支付 mapi.php
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
            request.setAll(params);
            log.info("【支付】发起支付请求,url:{} , params={}", payConfig.API_URL,params);
            // 地址 表单参数 返回类型字符串
            String result = restTemplate.postForObject(payConfig.API_URL, request, String.class);

            // 把JSON字符串 转成 PayResponse对象 并 返回
            return objectMapper.readValue(result, PayResponse.class);

        } catch (Exception e) {
            log.warn("发起支付请求失败, outTradeNo={}", outTradeNo, e);
            PayResponse resp = new PayResponse();
            resp.setCode(0);
            resp.setMsg("请求失败");
            return resp;
        }
    }

    /**
     * 主动查询支付平台订单状态（易支付 api.php 接口）。
     */
    @Override
    public Map<String, String> queryPay(String outTradeNo) {
        try {
            String url = payConfig.STATUS_URL
                    + "?act=order&pid=" + payConfig.PID
                    + "&key=" + payConfig.KEY
                    + "&out_trade_no=" + outTradeNo;

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(url, String.class);
            return objectMapper.readValue(result, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            log.warn("查询支付平台订单状态失败, outTradeNo={}", outTradeNo, e);
            return null;
        }
    }
}
