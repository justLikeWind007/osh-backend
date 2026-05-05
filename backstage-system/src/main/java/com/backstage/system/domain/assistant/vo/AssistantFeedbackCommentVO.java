package com.backstage.system.domain.assistant.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 助手反馈评论响应 VO
 *
 * @author backstage
 */
@Data
public class AssistantFeedbackCommentVO {

    /**
     * 评论 ID
     */
    private Long id;

    /**
     * 反馈 ID
     */
    private Long feedbackId;

    /**
     * 评论用户 ID
     */
    private Long userId;

    /**
     * 评论用户名
     */
    private String userName;

    /**
     * 评论用户头像
     */
    private String userAvatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 父评论 ID（0 表示一级评论）
     */
    private Long parentId;

    /**
     * 根评论 ID（用于二级评论）
     */
    private Long rootId;

    /**
     * 回复的用户 ID
     */
    private Long replyToUserId;

    /**
     * 回复的用户名
     */
    private String replyToUserName;

    /**
     * 评论层级（1-一级评论，2-二级评论/回复）
     */
    private Integer commentLevel;

    /**
     * 是否管理员回复（0-否 1-是）
     */
    private Integer isAdminReply;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 二级评论列表（仅一级评论有此字段）
     */
    private List<AssistantFeedbackCommentVO> replies;
}
