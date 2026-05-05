package com.backstage.system.domain.assistant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈收藏实体
 *
 * @author backstage
 */
@Data
@TableName("assistant_feedback_favorite")
public class AssistantFeedbackFavorite {

    /**
     * 收藏 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 反馈 ID
     */
    private Long feedbackId;

    /**
     * 收藏用户 ID
     */
    private Long userId;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;
}
