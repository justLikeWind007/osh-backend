package com.backstage.system.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户发起的拼团记录VO（用于我的拼团列表展示）
 * 
 * @author system
 * @date 2026-05-05
 */
public class MyInitiatedGroupVO {
    
    /** 发起记录ID */
    private Long initiatedId;
    
    /** 关联订单号 */
    private String orderNo;
    
    /** 订单状态 */
    private String orderStatus;
    
    /** 订单状态文字 */
    private String orderStatusText;
    
    /** 最低成团人数 */
    private Integer minNum;
    
    /** 最多成团人数 */
    private Integer maxNum;
    
    /** 当前参团人数 */
    private Integer currentNum;
    
    /** 服务器使用时长（月） */
    private Integer duration;
    
    /** 自定义拼团价格 */
    private BigDecimal customPrice;
    
    /** 组团状态：0-招募中 1-已成团 2-已取消/过期 */
    private Integer groupStatus;
    
    /** 组团状态文字 */
    private String groupStatusText;
    
    /** 服务器IP地址 */
    private String serverIp;
    
    /** 服务器开始使用时间 */
    private LocalDateTime serverStartTime;
    
    /** 服务器到期时间 */
    private LocalDateTime serverExpireTime;
    
    /** 发起时间 */
    private LocalDateTime initiateTime;
    
    /** 招募截止时间 */
    private LocalDateTime expireTime;
    
    // Getter and Setter
    
    public Long getInitiatedId() {
        return initiatedId;
    }
    
    public void setInitiatedId(Long initiatedId) {
        this.initiatedId = initiatedId;
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
    
    public String getOrderStatusText() {
        return orderStatusText;
    }
    
    public void setOrderStatusText(String orderStatusText) {
        this.orderStatusText = orderStatusText;
    }
    
    public Integer getMinNum() {
        return minNum;
    }
    
    public void setMinNum(Integer minNum) {
        this.minNum = minNum;
    }
    
    public Integer getMaxNum() {
        return maxNum;
    }
    
    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }
    
    public Integer getCurrentNum() {
        return currentNum;
    }
    
    public void setCurrentNum(Integer currentNum) {
        this.currentNum = currentNum;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public BigDecimal getCustomPrice() {
        return customPrice;
    }
    
    public void setCustomPrice(BigDecimal customPrice) {
        this.customPrice = customPrice;
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
    
    public String getServerIp() {
        return serverIp;
    }
    
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
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
    
    public LocalDateTime getInitiateTime() {
        return initiateTime;
    }
    
    public void setInitiateTime(LocalDateTime initiateTime) {
        this.initiateTime = initiateTime;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}
