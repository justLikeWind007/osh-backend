package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/24
 * Time: 18:47
 */
@ApiModel(value = "改绑邮箱参数", description = "前端传递的改绑邮箱请求实体类")
public class UserChangeEmailDTO {
    @ApiModelProperty(
            value = "用户的唯一标识",
            required = true
    )
    private String uniqueId;

    @ApiModelProperty(
            value = "新的邮箱账号",
            required = true
    )
    private String newEmail;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    @Override
    public String toString() {
        return "UserChangeEmailDTO{" +
                "uniqueId='" + uniqueId + '\'' +
                ", newEmail='" + newEmail + '\'' +
                '}';
    }
}
