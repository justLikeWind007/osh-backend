package com.backstage.system.request.tool;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "工具购买下单请求")
public class ToolPurchaseCreateRequest {

    @NotNull(message = "工具ID不能为空")
    @ApiModelProperty(value = "工具ID", required = true, example = "1001")
    private Long toolId;

    @NotNull(message = "套餐ID不能为空")
    @ApiModelProperty(value = "套餐ID", required = true, example = "2001")
    private Long packageId;

    @NotNull(message = "支付方式不能为空")
    @ApiModelProperty(value = "支付方式：1-纯现金，3-现金+积分", required = true, example = "1")
    private Integer payType;

    @NotBlank(message = "支付渠道不能为空")
    @ApiModelProperty(value = "支付渠道，如 wxpay/alipay", required = true, example = "wxpay")
    private String channel;

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
