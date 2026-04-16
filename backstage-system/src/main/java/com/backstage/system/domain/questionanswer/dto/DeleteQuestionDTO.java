package com.backstage.system.domain.questionanswer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/7
 * Time: 13:31
 */
@ApiModel(description = "删除问题实体类")
public class DeleteQuestionDTO {
    @ApiModelProperty(
            value = "问题id",
            required = true
    )
    private Long questionId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "PublishQuestionDTO{" +
                "questionId=" + questionId +
                '}';
    }
}
