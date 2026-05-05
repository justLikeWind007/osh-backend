package com.backstage.system.domain.assistant.vo;

import lombok.Data;

/**
 * AI 助手初始化信息 VO
 *
 * @author backstage
 */
@Data
public class AssistantInitVO {

    /**
     * 用户是否已登录
     */
    private Boolean loggedIn;

    /**
     * 反馈功能是否可用
     */
    private Boolean feedbackEnabled;

    /**
     * 反馈功能提示信息
     */
    private String feedbackMessage;

    /**
     * 当前课程 ID（如果在课程上下文中）
     */
    private Long courseId;

    /**
     * 当前课程名称
     */
    private String courseName;

    /**
     * 课程问答功能是否可用
     */
    private Boolean courseQaEnabled;

    /**
     * 课程问答不可用原因说明
     */
    private String courseQaReason;
}
