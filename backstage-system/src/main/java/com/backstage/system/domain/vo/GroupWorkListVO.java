package com.backstage.system.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 组团记录列表VO（管理端全量查询用）
 *
 * @author system
 * @date 2026-04-30
 */
public class GroupWorkListVO {

    /** 组团记录ID */
    private Long groupWorkId;

    /** 关联活动ID */
    private Long activityId;

    /** 活动标题 */
    private String activityTitle;

    /** 参团用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 实际支付价格 */
    private BigDecimal actualPrice;

    /** 剩余可使用月数 */
    private BigDecimal remainingMonths;

    /** 组团状态：0-进行中 1-已成团 2-已取消/过期 */
    private Integer groupStatus;

    /** 组团状态文字 */
    private String groupStatusText;

    /** 参团时间 */
    private LocalDateTime joinTime;

    /** 服务器开始使用时间 */
    private LocalDateTime serverStartTime;

    /** 服务器到期时间 */
    private LocalDateTime serverExpireTime;

    /** 关联订单号 */
    private String orderNo;

    /** 订单状态 */
    private String orderStatus;

    // ==================== Getter / Setter ====================

    public Long getGroupWorkId() {
        return groupWorkId;
    }

    public void setGroupWorkId(Long groupWorkId) {
        this.groupWorkId = groupWorkId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    public BigDecimal getRemainingMonths() {
        return remainingMonths;
    }

    public void setRemainingMonths(BigDecimal remainingMonths) {
        this.remainingMonths = remainingMonths;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
    }

    public String getGroupStatusText() {
        return groupStatusText;
    }

    public void setGroupStatusText(String groupStatusText) {
        this.groupStatusText = groupStatusText;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public LocalDateTime getServerStartTime() {
        return serverStartTime;
    }

    public void setServerStartTime(LocalDateTime serverStartTime) {
        this.serverStartTime = serverStartTime;
    }

    public LocalDateTime getServerExpireTime() {
        return serverExpireTime;
    }

    public void setServerExpireTime(LocalDateTime serverExpireTime) {
        this.serverExpireTime = serverExpireTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
