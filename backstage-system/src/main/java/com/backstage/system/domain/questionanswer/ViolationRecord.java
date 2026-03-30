package com.backstage.system.domain.questionanswer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:21
 */
public class ViolationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 违规用户id
     */
    private Long userId;

    /**
     * 违规回答id
     */
    private Long answerId;

    /**
     * 所属问题id
     */
    private Long questionId;

    /**
     * 违规类型：1=乱答，2=广告，3=恶意灌水，4=其他
     */
    private Byte violationType;

    /**
     * 违规原因（管理员填写或系统自动判定）
     */
    private String reason;

    /**
     * 操作人id（管理员id，系统自动判定则为null）
     */
    private Long operatorId;

    /**
     * 状态：0=有效，1=已撤销，2=已过期
     */
    private Byte status;

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

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Byte getViolationType() {
        return violationType;
    }

    public void setViolationType(Byte violationType) {
        this.violationType = violationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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
        return "ViolationRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", answerId=" + answerId +
                ", questionId=" + questionId +
                ", violationType=" + violationType +
                ", reason='" + reason + '\'' +
                ", operatorId=" + operatorId +
                ", status=" + status +
                ", createdTime=" + createdTime +
                ", createdBy='" + createdBy + '\'' +
                ", updatedTime=" + updatedTime +
                ", updatedBy='" + updatedBy + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }
}
