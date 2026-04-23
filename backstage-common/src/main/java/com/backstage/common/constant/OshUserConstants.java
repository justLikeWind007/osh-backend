package com.backstage.common.constant;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/30
 * Time: 13:20
 */
public class OshUserConstants {
    /**
     * 用户ID
     */
    public static final String USER_ID = "userId";
    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 20;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String UNIQUE_ID = "uniqueId:";
    public static final String RE_UNIQUE_ID = "re:uniqueId:";
    public static final String EMAIL = "email";
    public static final String LOGIN_USER = "LoginUser:";
    public static final String TOKEN = "token";
    public static final String USER_INFO = "userInfo";
    public static final String ASSET = "asset";
    public static final String ROLE = "role";
    public static final String PERMISSION = "permission";
    public static final String LEVEL = "level";
    /**
     * 第一个字符必须是字母（大小写均可）
     * 后面跟着 3-19 个字符，可以是字母、数字或下划线
     * 总长度4-20个字符
     */
    public static final String USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9_]{3,19}$";
    /**
     * 密码必须包含大小写字母、数字
     * 可以包含特殊字符
     * 总长度8-20个字符
     */
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,20}$";
}
