package com.backstage.system.request.tool;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

@ApiModel(description = "工具购买手动关单请求")
public class ToolPurchaseCancelRequest {

    @NotBlank(message = "支付流水号不能为空")
    @ApiModelProperty(value = "支付流水号", required = true, example = "P20260517003")
    private String paymentNo;

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }
}
