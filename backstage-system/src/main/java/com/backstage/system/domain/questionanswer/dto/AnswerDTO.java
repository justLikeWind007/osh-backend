package com.backstage.system.domain.questionanswer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/2
 * Time: 20:03
 */
@ApiModel(description = "回答问题实体类")
public class AnswerDTO {
    @ApiModelProperty(
            value = "回答的问题的id",
            required = true
    )
    private Long questionId;
    @ApiModelProperty(
            value = "回答的内容",
            required = true
    )
    private String content;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AnswerDTO{" +
                "questionId=" + questionId +
                ", content='" + content + '\'' +
                '}';
    }
}
