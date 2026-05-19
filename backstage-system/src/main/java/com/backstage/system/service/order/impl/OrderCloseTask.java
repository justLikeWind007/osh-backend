package com.backstage.system.service.order.impl;

import com.backstage.system.domain.order.OshPayment;
import com.backstage.system.mapper.order.OshPaymentMapper;
import com.backstage.system.service.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderCloseTask {

    private static final Logger log = LoggerFactory.getLogger(OrderCloseTask.class);

    @Resource
    private OshPaymentMapper oshPaymentMapper;

    @Resource
    private OrderService orderService;

    @Scheduled(cron = "0 * * * * ?")
    public void closeExpiredPendingPayments() {
        List<OshPayment> payments = oshPaymentMapper.selectExpiredPendingPayments(LocalDateTime.now());
        if (payments == null || payments.isEmpty()) {
            return;
        }
        log.info("扫描到超时待支付订单数量: {}", payments.size());
        for (OshPayment payment : payments) {
            if (payment == null || payment.getPaymentNo() == null) {
                continue;
            }
            try {
                orderService.cancelPayment(payment.getPaymentNo());
            } catch (Exception ex) {
                log.warn("关闭超时支付订单失败, paymentNo={}, error={}", payment.getPaymentNo(), ex.getMessage(), ex);
            }
        }
    }
}
