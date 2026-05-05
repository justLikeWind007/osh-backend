package com.backstage.system.domain.assistant.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈处理记录响应 VO
 *
 * @author backstage
 */
@Data
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
}
