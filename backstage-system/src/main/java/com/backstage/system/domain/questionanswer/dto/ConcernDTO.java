package com.backstage.system.domain.questionanswer.dto;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/29
 * Time: 18:47
 */
public class ConcernDTO {
    private Long questionId;

    public Long getQuestionId() {
        return questionId;
    }

    @Override
    public String toString() {
        return "ConcernDTO{" +
                "questionId=" + questionId +
                '}';
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
