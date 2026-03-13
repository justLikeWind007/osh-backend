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
public class ForgetDTO {
    @ApiModelProperty(
            value = "邮箱",
            example = "admin123",
            required = true
    )
    private String email;
    @ApiModelProperty(
            value = "验证码",
            example = "admin123",
            required = true
    )
    private String code;
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
        return "ForgetDTO{" +
                "email='" + email + '\'' +
                ", code='" + code + '\'' +
                ", password='" + password + '\'' +
                ", repassword='" + repassword + '\'' +
                '}';
    }
}
