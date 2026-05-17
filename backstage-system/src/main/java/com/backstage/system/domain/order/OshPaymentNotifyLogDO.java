package com.backstage.system.domain.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class OshPaymentNotifyLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonProperty("payment_no")
    private String paymentNo;

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("platform_trade_no")
    private String platformTradeNo;

    @JsonProperty("notify_payload")
    private String notifyPayload;

    @JsonProperty("sign_valid")
    private Integer signValid;

    @JsonProperty("process_status")
    private Integer processStatus;

    @JsonProperty("error_msg")
    private String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonProperty("created_time")
    private Date createdTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPlatformTradeNo() {
        return platformTradeNo;
    }

    public void setPlatformTradeNo(String platformTradeNo) {
        this.platformTradeNo = platformTradeNo;
    }

    public String getNotifyPayload() {
        return notifyPayload;
    }

    public void setNotifyPayload(String notifyPayload) {
        this.notifyPayload = notifyPayload;
    }

    public Integer getSignValid() {
        return signValid;
    }

    public void setSignValid(Integer signValid) {
        this.signValid = signValid;
    }

    public Integer getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(Integer processStatus) {
        this.processStatus = processStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
