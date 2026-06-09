package com.backstage.system.domain.vo.pay;

import java.math.BigDecimal;

/**
 * 前端创建支付响应。
 */
public class PayCreateRespVO {

    private int code;

    private String msg;

    private boolean needPay;

    private String orderNo;

    private String paymentNo;

    private String outTradeNo;

    private String payStatus;

    private BigDecimal price;

    private String expireTime;

    private Integer closeExpireMinutes;

    private String qrcode;

    private String payUrl;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

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

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
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

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
}
