package com.backstage.system.domain.servergroup;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户发起拼团记录实体
 * 
 * @author system
 * @date 2026-05-05
 */
public class OshGroupUserInitiated {
    
    /** 发起记录ID（主键） */
    private Long id;
    
    /** 发起人用户ID */
    private Long userId;
    
    /** 关联活动模板ID */
    private Long activityId;
    
    /** 活动标题（可选，用于自定义标题） */
    private String title;
    
    /** 活动封面（可选） */
    private String cover;
    
    /** 关联订单ID */
    private Long orderId;
    
    /** 最低成团人数 */
    private Integer minNum;
    
    /** 最多成团人数 */
    private Integer maxNum;
    
    /** 服务器使用时长（月） */
    private Integer duration;
    
    /** 自定义拼团价格（总价） */
    private BigDecimal customPrice;
    
    /** 组团状态：0-招募中 1-已成团 2-已结束 */
    private Integer groupStatus;
    
    /** 当前参团人数（含发起人自己） */
    private Integer currentNum;
    
    /** 服务器IP地址 */
    private String serverIp;
    
    /** 服务器登录账号 */
    private String serverAccount;
    
    /** 服务器登录密码（AES加密存储） */
    private String serverPassword;
    
    /** 服务器开始使用时间 */
    private LocalDateTime serverStartTime;
    
    /** 服务器到期时间 */
    private LocalDateTime serverExpireTime;
    
    /** 发起时间 */
    private LocalDateTime initiateTime;
    
    /** 招募截止时间 */
    private LocalDateTime expireTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 逻辑删除：0-正常 1-已删除 */
    private Integer deleteFlag;
    
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
    
    public Long getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCover() {
        return cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
    
    public Integer getCurrentNum() {
        return currentNum;
    }
    
    public void setCurrentNum(Integer currentNum) {
        this.currentNum = currentNum;
    }
    
    public String getServerIp() {
        return serverIp;
    }
    
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
    
    public String getServerAccount() {
        return serverAccount;
    }
    
    public void setServerAccount(String serverAccount) {
        this.serverAccount = serverAccount;
    }
    
    public String getServerPassword() {
        return serverPassword;
    }
    
    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
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
    
    public Integer getDeleteFlag() {
        return deleteFlag;
    }
    
    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
