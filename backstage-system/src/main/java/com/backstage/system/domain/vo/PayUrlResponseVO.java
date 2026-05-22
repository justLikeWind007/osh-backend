package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 支付链接响应VO
 */
@ApiModel(description = "支付链接响应")
public class PayUrlResponseVO {
    
    @ApiModelProperty("订单号")
    private String orderNo;
    
    @ApiModelProperty("支付链接")
    private String payUrl;
    
    @ApiModelProperty("提示信息")
    private String message;
    
    // Getter and Setter
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getPayUrl() {
        return payUrl;
    }
    
    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
