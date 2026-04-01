package com.backstage.system.domain.questionanswer;

import java.io.Serializable;
import java.time.LocalDateTime;
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
    private Long resourceNo;

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
    private LocalDateTime createdTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 逻辑删除：0=未删除，1=已删除
     */
    private Integer isDelete;

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

    public Long getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(Long resourceNo) {
        this.resourceNo = resourceNo;
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", userId=" + userId +
                ", resourceType=" + resourceType +
                ", resourceId=" + resourceNo +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isPaidOnly=" + isPaidOnly +
                ", status=" + status +
                ", solvedAnswerId=" + solvedAnswerId +
                ", viewCount=" + viewCount +
                ", followCount=" + followCount +
                ", createdTime=" + createdTime +
                ", createdBy='" + createdBy + '\'' +
                ", updatedTime=" + updatedTime +
                ", updatedBy='" + updatedBy + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }
}