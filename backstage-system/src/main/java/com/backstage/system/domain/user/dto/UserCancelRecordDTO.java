package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/9
 * Time: 20:00
 */
@ApiModel(description = "取消标记违规实体类")
public class UserCancelRecordDTO {
    @ApiModelProperty(
            value = "违规用户id",
            required = true
    )
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserCancelRecordDTO{" +
                "userId=" + userId +
                '}';
    }
}
