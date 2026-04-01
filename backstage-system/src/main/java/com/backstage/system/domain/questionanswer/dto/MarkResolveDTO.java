package com.backstage.system.domain.questionanswer.dto;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/29
 * Time: 18:39
 */
public class MarkResolveDTO {
    private Long questionId;
    private Long answerId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    @Override
    public String toString() {
        return "MarkResolveDTO{" +
                "questionId=" + questionId +
                ", answerId=" + answerId +
                '}';
    }
}
