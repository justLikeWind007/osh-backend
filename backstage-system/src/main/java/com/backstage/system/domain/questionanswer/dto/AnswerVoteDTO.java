package com.backstage.system.domain.questionanswer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/4
 * Time: 16:18
 */
@ApiModel(description = "点赞回答实体类")
public class AnswerVoteDTO {
    @ApiModelProperty(
            value = "回答的内容",
            required = true
    )
    private Long answerId;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    @Override
    public String toString() {
        return "AnswerVoteDTO{" +
                "answerId=" + answerId +
                '}';
    }
}
