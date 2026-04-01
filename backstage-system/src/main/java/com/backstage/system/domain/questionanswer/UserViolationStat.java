package com.backstage.system.domain.questionanswer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:22
 */
public class UserViolationStat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 当前有效违规次数
     */
    private Integer violationCount;

    /**
     * 是否灰名单：0=否，1=是
     */
    private Byte isGraylisted;

    /**
     * 是否封号：0=否，1=是
     */
    private Byte isBanned;

    /**
     * 违规原因（默认违规三次封号）
     */
    private String reason;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getViolationCount() {
        return violationCount;
    }

    public void setViolationCount(Integer violationCount) {
        this.violationCount = violationCount;
    }

    public Byte getIsGraylisted() {
        return isGraylisted;
    }

    public void setIsGraylisted(Byte isGraylisted) {
        this.isGraylisted = isGraylisted;
    }

    public Byte getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Byte isBanned) {
        this.isBanned = isBanned;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
}
