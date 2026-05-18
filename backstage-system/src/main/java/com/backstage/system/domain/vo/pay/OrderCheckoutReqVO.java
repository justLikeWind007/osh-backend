package com.backstage.system.domain.vo.pay;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 订单结算请求参数。
 */
public class OrderCheckoutReqVO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "商品类型不能为空")
    private Integer productType;

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotBlank(message = "商品名称不能为空")
    private String productName;

    private Integer purchaseMode;

    private Long activityId;

    @NotNull(message = "原价不能为空且不能小于0")
    @DecimalMin(value = "0.00", message = "原价不能为空且不能小于0")
    private BigDecimal originalAmount;

    private BigDecimal discountAmount;

    @NotNull(message = "应付金额不能为空且不能小于0")
    @DecimalMin(value = "0.00", message = "应付金额不能为空且不能小于0")
    private BigDecimal payableAmount;

    private Long couponId;

    private String channel;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPurchaseMode() {
        return purchaseMode;
    }

    public void setPurchaseMode(Integer purchaseMode) {
        this.purchaseMode = purchaseMode;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    /** 支付渠道字符串标识，如 wxpay/alipay/bank */
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
