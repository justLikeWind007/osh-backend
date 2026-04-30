package com.backstage.system.domain.questionanswer.dto;

import com.backstage.common.annotation.OshResourceId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/4
 * Time: 15:19
 */
@ApiModel(description = "查看问题详情实体类")
public class QueryQuestionDetailDTO {
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
        return "QueryQuestionDetailDTO{" +
                "questionId=" + questionId +
                '}';
    }
}
