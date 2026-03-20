package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 17:03
 */
@ApiModel(value = "账号登录参数", description = "前端传递的登录请求实体类")
public class UserLoginDTO {
    @ApiModelProperty(
            value = "用户名或邮箱",
            required = true
    )
    private String name;
    @ApiModelProperty(
            value = "用户密码或唯一标识",
            example = "admin123",
            required = true
    )
    private String pid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "UserLoginDTO{" +
                "name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                '}';
    }
}
