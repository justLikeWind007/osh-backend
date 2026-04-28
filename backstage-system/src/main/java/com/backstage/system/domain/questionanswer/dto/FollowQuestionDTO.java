package com.backstage.system.domain.questionanswer.dto;

import com.backstage.common.annotation.OshResourceId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/2
 * Time: 16:29
 */
@ApiModel(description = "关注问题实体类")
public class FollowQuestionDTO {
    @ApiModelProperty(
            value = "问题id",
            required = true
    )
    @OshResourceId
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
