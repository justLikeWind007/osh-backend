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

    /** 本单使用积分数量 */
    private Long pointsUsed;

    /** 积分抵扣金额 */
    private BigDecimal pointsDeductAmount;

    /** 抵扣后的剩余积分 */
    private Long remainingPoints;

    private String expireTime;

    private Integer closeExpireMinutes;

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

    public Long getPointsUsed() {
        return pointsUsed;
    }

    public void setPointsUsed(Long pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public BigDecimal getPointsDeductAmount() {
        return pointsDeductAmount;
    }

    public void setPointsDeductAmount(BigDecimal pointsDeductAmount) {
        this.pointsDeductAmount = pointsDeductAmount;
    }

    public Long getRemainingPoints() {
        return remainingPoints;
    }

    public void setRemainingPoints(Long remainingPoints) {
        this.remainingPoints = remainingPoints;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getCloseExpireMinutes() {
        return closeExpireMinutes;
    }

    public void setCloseExpireMinutes(Integer closeExpireMinutes) {
        this.closeExpireMinutes = closeExpireMinutes;
    }

    public OrderPaymentInfo getPayment() {
        return payment;
    }

    public void setPayment(OrderPaymentInfo payment) {
        this.payment = payment;
    }
}
