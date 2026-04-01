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
    public static final int USERNAME_MIN_LENGTH = 8;
    public static final int USERNAME_MAX_LENGTH = 16;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 16;

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String UNIQUE_ID = "uniqueId:";
    public static final String RE_UNIQUE_ID = "re:uniqueId:";
    public static final String EMAIL = "email";
    public static final String LOGIN_USER = "LoginUser:";

    public static final String USERNAME_PATTERN = "^[a-zA-Z][a-zA-Z0-9_]{7,15}$";
    public static final String PASSWORD_PATTERN = "^[a-zA-Z][a-zA-Z0-9_]{7,15}$";
}
