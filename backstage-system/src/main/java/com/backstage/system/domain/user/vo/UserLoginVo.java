package com.backstage.system.domain.user.vo;

import com.backstage.system.domain.user.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/12
 * Time: 20:16
 */
public class UserLoginVo extends User {
    private String token;

    private List<String> role;

    private List<String> permissionList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

    @Override
    public String toString() {
        return "UserLoginVo{" +
                "token='" + token + '\'' +
                ", role=" + role +
                ", permissionList=" + permissionList +
                '}';
    }
}
