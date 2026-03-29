package com.backstage.system.domain.questionanswer;

import java.io.Serializable;
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
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    // 无参构造方法
    public UserViolationStat() {
    }

    // 全参构造方法
    public UserViolationStat(Long userId, Integer violationCount, Byte isGraylisted,
                             Byte isBanned, String reason, Date createdTime, Date updatedTime) {
        this.userId = userId;
        this.violationCount = violationCount;
        this.isGraylisted = isGraylisted;
        this.isBanned = isBanned;
        this.reason = reason;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // getter和setter方法
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
        return "UserViolationStat{" +
                "userId=" + userId +
                ", violationCount=" + violationCount +
                ", isGraylisted=" + isGraylisted +
                ", isBanned=" + isBanned +
                ", reason='" + reason + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
