package com.backstage.system.domain.assistant.vo;


/**
 * AI 助手初始化信息 VO
 *
 * @author backstage
 */
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

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Boolean getFeedbackEnabled() {
        return feedbackEnabled;
    }

    public void setFeedbackEnabled(Boolean feedbackEnabled) {
        this.feedbackEnabled = feedbackEnabled;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Boolean getCourseQaEnabled() {
        return courseQaEnabled;
    }

    public void setCourseQaEnabled(Boolean courseQaEnabled) {
        this.courseQaEnabled = courseQaEnabled;
    }

    public String getCourseQaReason() {
        return courseQaReason;
    }

    public void setCourseQaReason(String courseQaReason) {
        this.courseQaReason = courseQaReason;
    }
}
