package com.backstage.system.domain.assistant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 助手反馈评论实体
 *
 * @author backstage
 */
@Data
@TableName("assistant_feedback_comment")
public class AssistantFeedbackComment {

    /**
     * 评论 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 删除标记（0-未删除 1-已删除）
     */
    @TableLogic
    private Integer deleteFlag;
}
