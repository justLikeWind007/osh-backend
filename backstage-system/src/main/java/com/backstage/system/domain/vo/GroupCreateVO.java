package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 发起拼团响应VO
 * 
 * @author system
 * @date 2026-04-30
 */
@ApiModel("发起拼团响应结果")
public class GroupCreateVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 组团ID */
    @ApiModelProperty("组团ID")
    private Long workId;
    
    /** 订单号 */
    @ApiModelProperty("订单号")
    private String orderNo;
    
    /** 支付链接（如果需要立即支付） */
    @ApiModelProperty("支付链接")
    private String payUrl;
    
    public Long getWorkId() {
        return workId;
    }
    
    public void setWorkId(Long workId) {
        this.workId = workId;
    }
    
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
}
