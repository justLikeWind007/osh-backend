package com.backstage.system.domain.assistant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 助手反馈分类实体
 *
 * @author backstage
 */
@Data
@TableName("assistant_feedback_category")
public class AssistantFeedbackCategory {

    /**
     * 分类 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类代码（唯一标识）
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 驱动力（用户提交该类型反馈的动机）
     */
    private String driveForce;

    /**
     * 期望结果（用户期望得到的反馈）
     */
    private String expectedResult;

    /**
     * 语气倾向（该类型反馈的典型语气）
     */
    private String toneTendency;

    /**
     * 分类图标（Emoji 或图标类名）
     */
    private String icon;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 是否仅管理员可用（0-否 1-是）
     */
    private Integer isAdminOnly;

    /**
     * 是否允许评论（0-否 1-是）
     */
    private Integer allowComment;

    /**
     * 是否启用（0-否 1-是）
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
