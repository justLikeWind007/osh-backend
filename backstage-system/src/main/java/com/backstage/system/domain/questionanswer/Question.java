package com.backstage.system.domain.questionanswer;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:17
 */

public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 提问者id
     */
    private Long userId;

    /**
     * 资源类型：0=无，1=网站，2=课程，3=电子书，4=其他
     */
    private Byte resourceType;

    /**
     * 资源编号（resource_type=0时为空）
     */
    private Long resourceId;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 是否仅付费用户专属答疑：0=普通免费，1=付费专属
     */
    private Byte isPaidOnly;

    /**
     * 状态：0=待回答，1=已解决，2=已关闭
     */
    private Byte status;

    /**
     * 标记为已解决的回答id
     */
    private Long solvedAnswerId;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 关注数
     */
    private Integer followCount;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    // 无参构造方法
    public Question() {
    }

    // 全参构造方法
    public Question(Long id, Long userId, Byte resourceType, Long resourceId, String title,
                    String content, Byte isPaidOnly, Byte status, Long solvedAnswerId,
                    Integer viewCount, Integer followCount, Date createdTime, Date updatedTime) {
        this.id = id;
        this.userId = userId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.title = title;
        this.content = content;
        this.isPaidOnly = isPaidOnly;
        this.status = status;
        this.solvedAnswerId = solvedAnswerId;
        this.viewCount = viewCount;
        this.followCount = followCount;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // getter和setter方法
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

    public Byte getResourceType() {
        return resourceType;
    }

    public void setResourceType(Byte resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Byte getIsPaidOnly() {
        return isPaidOnly;
    }

    public void setIsPaidOnly(Byte isPaidOnly) {
        this.isPaidOnly = isPaidOnly;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getSolvedAnswerId() {
        return solvedAnswerId;
    }

    public void setSolvedAnswerId(Long solvedAnswerId) {
        this.solvedAnswerId = solvedAnswerId;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }


    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", userId=" + userId +
                ", resourceType=" + resourceType +
                ", resourceId=" + resourceId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isPaidOnly=" + isPaidOnly +
                ", status=" + status +
                ", solvedAnswerId=" + solvedAnswerId +
                ", viewCount=" + viewCount +
                ", followCount=" + followCount +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}