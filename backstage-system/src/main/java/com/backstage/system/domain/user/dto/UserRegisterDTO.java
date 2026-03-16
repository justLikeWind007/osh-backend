package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 17:19
 */
@ApiModel(value = "账号注册参数", description = "前端传递的账号注册请求实体类")
public class UserRegisterDTO {
    @ApiModelProperty(
            value = "用户名",
            required = true
    )
    private String username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        return "UserRegisterDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", repassword='" + repassword + '\'' +
                '}';
    }
}
