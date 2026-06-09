package com.backstage.system.domain.servergroup;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务器拼团活动实体
 * 
 * @author system
 * @date 2026-04-18
 */
public class OshGroupActivity {
    
    /** 拼团活动ID（主键） */
    private Long id;
    
    /** 拼团活动标题 */
    private String title;
    
    /** 拼团类型：server-服务器（固定值） */
    private String type;
    
    /** 服务器CPU配置 */
    private String cpu;
    
    /** 服务器内存配置 */
    private String memory;
    
    /** 服务器存储配置 */
    private String storage;
    
    /** 基础拼团价格（按月计算，完整周期价格） */
    private BigDecimal basePrice;
    
    /** 服务器总使用时长（月） */
    private Integer totalDuration;
    
    /** 拼团所需最低人数 */
    private Integer groupMinNum;
    
    /** 拼团人数上限 */
    private Integer groupMaxNum;
    
    /** 当前已参团人数 */
    private Integer currentNum;
    
    /** 活动状态：1-进行中 2-拼团成功 3-已结束 */
    private Integer status;
    
    /** 拼团开始时间 */
    private LocalDateTime startTime;
    
    /** 服务器开始使用时间（成团后有值） */
    private LocalDateTime serverStartTime;
    
    /** 服务器使用结束时间（成团后有值） */
    private LocalDateTime serverEndTime;
    
    /** 服务器配置教程URL */
    private String serverTutorialUrl;
    
    /** 管理员联系方式 */
    private String adminContact;
    
    /** 排序权重 */
    private Integer sortOrder;
    
    /** 封面图片路径 */
    private String cover;
    
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getCpu() {
        return cpu;
    }
    
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }
    
    public String getMemory() {
        return memory;
    }
    
    public void setMemory(String memory) {
        this.memory = memory;
    }
    
    public String getStorage() {
        return storage;
    }
    
    public void setStorage(String storage) {
        this.storage = storage;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    
    public Integer getTotalDuration() {
        return totalDuration;
    }
    
    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }
    
    public Integer getGroupMinNum() {
        return groupMinNum;
    }
    
    public void setGroupMinNum(Integer groupMinNum) {
        this.groupMinNum = groupMinNum;
    }
    
    public Integer getGroupMaxNum() {
        return groupMaxNum;
    }
    
    public void setGroupMaxNum(Integer groupMaxNum) {
        this.groupMaxNum = groupMaxNum;
    }
    
    public Integer getCurrentNum() {
        return currentNum;
    }
    
    public void setCurrentNum(Integer currentNum) {
        this.currentNum = currentNum;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getServerStartTime() {
        return serverStartTime;
    }
    
    public void setServerStartTime(LocalDateTime serverStartTime) {
        this.serverStartTime = serverStartTime;
    }
    
    public LocalDateTime getServerEndTime() {
        return serverEndTime;
    }
    
    public void setServerEndTime(LocalDateTime serverEndTime) {
        this.serverEndTime = serverEndTime;
    }
    
    public String getServerTutorialUrl() {
        return serverTutorialUrl;
    }
    
    public void setServerTutorialUrl(String serverTutorialUrl) {
        this.serverTutorialUrl = serverTutorialUrl;
    }
    
    public String getAdminContact() {
        return adminContact;
    }
    
    public void setAdminContact(String adminContact) {
        this.adminContact = adminContact;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getCover() {
        return cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
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
