package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 21:38
 */
@ApiModel(value = "找回密码参数", description = "前端传递的找回密码请求实体类")
public class UserForgetDTO {
    @ApiModelProperty(
            value = "用户的唯一标识",
            required = true
    )
    private String uniqueId;
    @ApiModelProperty(
            value = "用户密码",
            example = "admin123",
            required = true
    )
    private String password;
    @ApiModelProperty(
            value = "确认密码",
            example = "admin123",
            required = true
    )
    private String repassword;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }

    @Override
    public String toString() {
        return "UserForgetDTO{" +
                "uniqueId='" + uniqueId + '\'' +
                ", password='" + password + '\'' +
                ", repassword='" + repassword + '\'' +
                '}';
    }
}
