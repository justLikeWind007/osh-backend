package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 参团响应VO
 */
@ApiModel(description = "参团响应")
public class JoinGroupResponseVO {
    
    @ApiModelProperty("订单号")
    private String orderNo;
    
    @ApiModelProperty("是否需要支付：true-需要支付 false-不需要支付")
    private Boolean needPay;
    
    @ApiModelProperty("订单金额")
    private BigDecimal price;
    
    @ApiModelProperty("支付方式：wechat-微信支付 alipay-支付宝")
    private String payMethod;
    
    @ApiModelProperty("提示信息")
    private String message;
    
    // Getter and Setter
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public Boolean getNeedPay() {
        return needPay;
    }
    
    public void setNeedPay(Boolean needPay) {
        this.needPay = needPay;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getPayMethod() {
        return payMethod;
    }
    
    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
