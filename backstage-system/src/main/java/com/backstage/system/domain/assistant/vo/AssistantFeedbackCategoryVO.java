package com.backstage.system.domain.assistant.vo;

import lombok.Data;

/**
 * AI 助手反馈分类响应 VO
 *
 * @author backstage
 */
@Data
public class AssistantFeedbackCategoryVO {

    /**
     * 分类 ID
     */
    private Long id;

    /**
     * 分类代码
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
     * 驱动力
     */
    private String driveForce;

    /**
     * 期望结果
     */
    private String expectedResult;

    /**
     * 语气倾向
     */
    private String toneTendency;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否允许评论（0-否 1-是）
     */
    private Integer allowComment;
}
