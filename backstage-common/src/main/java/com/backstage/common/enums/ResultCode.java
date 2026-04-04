package com.backstage.common.enums;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/30
 * Time: 13:13
 */
public enum ResultCode {
    SUCCESS(1000,"操作成功"),

    ERROR(2000,"服务器繁忙请稍后重试"),

    //操作失败,但是服务器不存在异常
    FAILED (3000, "操作失败"),
    FAILED_UNAUTHORIZED (3001, "未授权"),
    FAILED_PARAMS_VALIDATE (3002, "参数校验失败"),
    FAILED_PERMISSION_ERROR (3003, "权限校验失败"),
    FAILED_NOT_EXISTS (3004, "资源不存在"),
    FAILED_ALREADY_EXISTS (3005, "资源已存在"),

    FAILED_TOKEN_EXPIRED (3100, "登录已过期，请重新登录"),
    AILED_USER_EXISTS (3101, "用户已存在"),
    FAILED_USER_NOT_EXISTS (3102, "用户不存在"),
    FAILED_USER_BANNED (3103, "您已被列入黑名单, 请联系管理员."),
    FAILED_USER_UNIQUEID_ERROR (3104, "用户唯一标识错误"),
    FAILED_USER_PASSWORD_ERROR (3105, "用户密码错误"),
    FAILED_USER_PASSWORD_NOT_MATCH (3106, "两次输入密码不一致"),
    FAILED_USER_EMAIL_BOUND (3107, "邮箱已被绑定"),
    FAILED_FREQUENT(3108,"操作频繁,请稍后重试"),
    FAILED_TIME_LIMIT(3109,"当天请求次数已达到上限"),

    FAILED_USER_NAME_OR_PASSWORD_EMPTY (3200, "用户名或密码为空"),
    FAILED_USER_PASSWORD_NOT_MATCHES (3201, "密码不符合要求"),
    FAILED_USER_USERNAME_NOT_IN_RANGE (3202, "用户名不符合要求"),
    ;
    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
