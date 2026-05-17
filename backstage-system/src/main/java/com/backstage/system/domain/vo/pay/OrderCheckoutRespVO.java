package com.backstage.system.domain.vo.pay;

import com.backstage.system.domain.order.OrderPaymentInfo;

import java.math.BigDecimal;

/**
 * 订单结算响应结果。
 */
public class OrderCheckoutRespVO {

    private boolean needPay;

    private String orderNo;

    private String paymentNo;

    private String payStatus;

    private BigDecimal price;

    private OrderPaymentInfo payment;

    public boolean isNeedPay() {
        return needPay;
    }

    public void setNeedPay(boolean needPay) {
        this.needPay = needPay;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OrderPaymentInfo getPayment() {
        return payment;
    }

    public void setPayment(OrderPaymentInfo payment) {
        this.payment = payment;
    }
}
