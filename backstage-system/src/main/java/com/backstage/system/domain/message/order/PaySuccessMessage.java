package com.backstage.system.domain.message.order;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 */
public class PaySuccessMessage {


    @ApiModelProperty(value = "订单号")
    private String orderNo;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
