package com.backstage.system.domain.vo.tool;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(description = "工具购买套餐")
public class ToolPurchasePackageVO {

    @ApiModelProperty(value = "套餐ID", example = "2001")
    private Long packageId;

    @ApiModelProperty(value = "套餐名称", example = "体验包")
    private String packageName;

    @ApiModelProperty(value = "购买后增加次数", example = "10")
    private Integer useCount;

    @ApiModelProperty(value = "现金金额", example = "9.90")
    private BigDecimal cashAmount;

    @ApiModelProperty(value = "积分金额", example = "100")
    private Integer pointAmount;

    @ApiModelProperty(value = "支付类型：1-纯现金，3-现金+积分", example = "3")
    private Integer payType;

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Integer getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(Integer pointAmount) {
        this.pointAmount = pointAmount;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }
}
