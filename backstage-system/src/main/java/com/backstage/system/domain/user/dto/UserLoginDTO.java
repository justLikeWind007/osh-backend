package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/7
 * Time: 17:03
 */
@ApiModel(value = "账号登录参数", description = "前端传递的登录请求实体类")
public class UserLoginDTO {
    @ApiModelProperty(
            value = "用户名或邮箱",
            required = true
    )
    private String username;
    @ApiModelProperty(
            value = "用户密码或唯一标识",
            example = "admin123",
            required = true
    )
    private String password;



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

    @Override
    public String toString() {
        return "UserLoginDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
