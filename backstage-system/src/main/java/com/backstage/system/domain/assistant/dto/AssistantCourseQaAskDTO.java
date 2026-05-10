package com.backstage.system.domain.assistant.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * AI 助手课程问答请求 DTO
 *
 * @author backstage
 */
public class AssistantCourseQaAskDTO {

    /**
     * 课程 ID
     */
    @NotNull(message = "courseId 不能为空")
    private Long courseId;

    /**
     * 用户提问内容
     */
    @NotBlank(message = "问题不能为空")
    private String question;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
