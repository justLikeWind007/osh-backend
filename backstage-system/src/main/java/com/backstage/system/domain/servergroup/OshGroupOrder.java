package com.backstage.system.domain.servergroup;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务器拼团订单实体
 * 
 * @author system
 * @date 2026-04-18
 */
public class OshGroupOrder {
    
    /** 订单ID（主键） */
    private Long id;
    
    /** 下单用户ID */
    private Long userId;
    
    /** 关联拼团活动ID */
    private Long groupActivityId;
    
    /** 关联参团记录ID */
    private Long groupWorkId;
    
    /** 订单编号（唯一） */
    private String orderNo;
    
    /** 实际支付价格（动态计算） */
    private BigDecimal price;
    
    /** 基础拼团价格（完整周期） */
    private BigDecimal basePrice;
    
    /** 参团时剩余月数 */
    private BigDecimal remainingMonths;
    
    /** 订单状态：pending-待支付 paid-已支付 success-拼团成功 refunded-已退款 cancelled-已取消 */
    private String status;
    
    /** 支付方式：wechat-微信 alipay-支付宝 */
    private String payMethod;
    
    /** 支付时间 */
    private LocalDateTime payTime;
    
    /** 备注 */
    private String remark;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    // Getter and Setter
    
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
    
    public Long getGroupActivityId() {
        return groupActivityId;
    }
    
    public void setGroupActivityId(Long groupActivityId) {
        this.groupActivityId = groupActivityId;
    }
    
    public Long getGroupWorkId() {
        return groupWorkId;
    }
    
    public void setGroupWorkId(Long groupWorkId) {
        this.groupWorkId = groupWorkId;
    }
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    
    public BigDecimal getRemainingMonths() {
        return remainingMonths;
    }
    
    public void setRemainingMonths(BigDecimal remainingMonths) {
        this.remainingMonths = remainingMonths;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPayMethod() {
        return payMethod;
    }
    
    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
    
    public LocalDateTime getPayTime() {
        return payTime;
    }
    
    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
