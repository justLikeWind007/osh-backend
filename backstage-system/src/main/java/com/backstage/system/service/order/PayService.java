package com.backstage.system.service.order;

import com.backstage.system.domain.vo.order.PayResponse;

import java.util.Map;

public interface PayService {

    /**
     * 向支付平台发起支付请求。
     *
     * @param outTradeNo 支付流水号
     * @param name 商品名称
     * @param money 支付金额
     * @param clientIp 客户端IP
     * @param channel 支付渠道标识（wxpay/alipay/bank等）
     * @return 支付平台响应
     */
    PayResponse createPay(String outTradeNo, String name, String money, String clientIp, String channel);

    /**
     * 主动查询支付平台订单状态。
     *
     * @param outTradeNo 支付流水号
     * @return 支付平台返回的订单信息，查询失败返回 null
     */
    Map<String, String> queryPay(String outTradeNo);
}
