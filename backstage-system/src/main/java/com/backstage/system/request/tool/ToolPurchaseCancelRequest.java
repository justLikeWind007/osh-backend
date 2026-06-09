package com.backstage.system.request.tool;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "工具购买手动关单请求")
public class ToolPurchaseCancelRequest {

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号", required = true, example = "O20260517003")
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
