package com.backstage.system.domain.assistant;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 反馈标签关联实体
 *
 * @author backstage
 */
@TableName("assistant_feedback_tag_rel")
public class AssistantFeedbackTagRel extends OSHBaseEntity {

    /**
     * 关联 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 反馈 ID
     */
    private Long feedbackId;

    /**
     * 标签 ID
     */
    private Long tagId;

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

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
