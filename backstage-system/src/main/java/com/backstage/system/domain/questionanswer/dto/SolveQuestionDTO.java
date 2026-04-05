package com.backstage.system.domain.questionanswer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/29
 * Time: 18:39
 */
@ApiModel(description = "采纳回答实体类")
public class SolveQuestionDTO {
    @ApiModelProperty(
            value = "问题id",
            required = true
    )
    private Long questionId;
    @ApiModelProperty(
            value = "回答id",
            required = true
    )
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
        return "SolveQuestionDTO{" +
                "questionId=" + questionId +
                ", answerId=" + answerId +
                '}';
    }
}
