package com.backstage.system.domain.user.vo;

import com.backstage.system.domain.user.User;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/12
 * Time: 20:16
 */
public class UserLoginVo extends User {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserLoginVo{" +
                "token='" + token + '\'' +
                '}';
    }
}
