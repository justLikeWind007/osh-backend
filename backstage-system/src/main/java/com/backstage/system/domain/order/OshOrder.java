package com.backstage.system.domain.order;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class OshOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;

    private Integer status;

    /**
     * 商品类型
     */
    private Integer productType;

    /**
     * 商品 ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 购买方式
     */
    private Integer purchaseMode;


    /**
     * 活动 ID
     */
    private Long activityId;

    /**
     * 原价
     */
    private BigDecimal originalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 应付金额
     */
    private BigDecimal payableAmount;

    /**
     * 积分抵扣数量
     */
    private Long pointsAmount;

    /**
     * 积分抵扣金额
     */
    private BigDecimal pointsDeductAmount;

    /**
     * 优惠券 ID
     */
    private Long couponId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 支付时间
     */
    private LocalDateTime paidTime;

    /**
     * 关闭时间
     */
    private LocalDateTime closeTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Long getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(Long pointsAmount) {
        this.pointsAmount = pointsAmount;
    }

    public BigDecimal getPointsDeductAmount() {
        return pointsDeductAmount;
    }

    public void setPointsDeductAmount(BigDecimal pointsDeductAmount) {
        this.pointsDeductAmount = pointsDeductAmount;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(LocalDateTime paidTime) {
        this.paidTime = paidTime;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
