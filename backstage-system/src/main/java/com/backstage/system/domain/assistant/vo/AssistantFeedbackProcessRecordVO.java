package com.backstage.system.domain.assistant.vo;


import java.time.LocalDateTime;

/**
 * 反馈处理记录响应 VO
 *
 * @author backstage
 */
public class AssistantFeedbackProcessRecordVO {

    /**
     * 记录 ID
     */
    private Long id;

    /**
     * 反馈 ID
     */
    private Long feedbackId;

    /**
     * 变更前状态
     */
    private String fromStatus;

    /**
     * 变更前状态文案
     */
    private String fromStatusText;

    /**
     * 变更后状态
     */
    private String toStatus;

    /**
     * 变更后状态文案
     */
    private String toStatusText;

    /**
     * 操作人 ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 处理说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getFromStatusText() {
        return fromStatusText;
    }

    public void setFromStatusText(String fromStatusText) {
        this.fromStatusText = fromStatusText;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getToStatusText() {
        return toStatusText;
    }

    public void setToStatusText(String toStatusText) {
        this.toStatusText = toStatusText;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

}
