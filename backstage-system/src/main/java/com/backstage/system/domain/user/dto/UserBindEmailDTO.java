package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/12
 * Time: 19:55
 */
@ApiModel(value = "绑定邮箱参数", description = "前端传递的绑定邮箱请求实体类")
public class UserBindEmailDTO {
    @ApiModelProperty(
            value = "邮箱",
            required = true
    )
    private String email;
    @ApiModelProperty(
            value = "验证码",
            required = true
    )
    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "UserBindEmailDTO{" +
                "email='" + email + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
