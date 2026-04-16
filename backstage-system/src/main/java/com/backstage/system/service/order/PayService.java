package com.backstage.system.service.order;

import com.backstage.system.domain.vo.order.PayResponse;

public interface PayService{
    public PayResponse createPay(String outTradeNo, String name, String money, String clientIp);
}
