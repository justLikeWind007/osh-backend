package com.backstage.system.domain.assistant.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AI 助手反馈评论创建 DTO
 *
 * @author backstage
 */
@Data
public class AssistantFeedbackCommentDTO {

    /**
     * 反馈 ID（从路径参数获取，不需要前端传递）
     */
    private Long feedbackId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500个字符")
    private String content;

    /**
     * 父评论 ID（0 或 null 表示一级评论）
     */
    private Long parentId;

    /**
     * 回复的用户 ID（二级评论时必填）
     */
    private Long replyToUserId;

    /**
     * 回复的用户名（二级评论时必填）
     */
    private String replyToUserName;
}
