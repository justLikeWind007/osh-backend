package com.backstage.system.domain.questionanswer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/11
 * Time: 19:17
 */
@ApiModel(description = "编辑问题实体类")
public class EditQuestionDTO extends AddQuestionDTO{
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
        return "EditQuestionDTO{" +
                "questionId=" + questionId +
                '}';
    }
}
