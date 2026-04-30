package com.backstage.system.domain.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程购买状态 VO
 * 用于返回用户对课程的购买状态信息
 *
 * @author ruoyi
 * @date 2026-03-31
 */
public class CoursePurchaseStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 是否已购买
     */
    private Boolean isPurchased;

    /**
     * 是否已过期
     */
    private Boolean isExpired;

    /**
     * 购买时间
     */
    private Date purchaseTime;

    /**
     * 到期时间
     */
    private Date expireTime;

    /**
     * 服务类型（永久有效/年度/月度）
     */
    private String serviceType;

    /**
     * 订单状态（pendding/closed）
     */
    private String status;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单价格
     */
    private String price;

    /**
     * 未购买原因
     */
    private String reason;

    /**
     * 剩余天数（用于显示距离过期还有多少天）
     */
    private Long remainingDays;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Boolean getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(Boolean isPurchased) {
        this.isPurchased = isPurchased;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Long remainingDays) {
        this.remainingDays = remainingDays;
    }

    @Override
    public String toString() {
        return "CoursePurchaseStatusVO{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", isPurchased=" + isPurchased +
                ", isExpired=" + isExpired +
                ", purchaseTime=" + purchaseTime +
                ", expireTime=" + expireTime +
                ", serviceType='" + serviceType + '\'' +
                ", status='" + status + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", price='" + price + '\'' +
                ", reason='" + reason + '\'' +
                ", remainingDays=" + remainingDays +
                '}';
    }
}
