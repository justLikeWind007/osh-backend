package com.backstage.system.domain.assistant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈点赞实体
 *
 * @author backstage
 */
@Data
@TableName("assistant_feedback_like")
public class AssistantFeedbackLike {

    /**
     * 点赞 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 反馈 ID
     */
    private Long feedbackId;

    /**
     * 点赞用户 ID
     */
    private Long userId;

    /**
     * 点赞时间
     */
    private LocalDateTime createTime;
}
