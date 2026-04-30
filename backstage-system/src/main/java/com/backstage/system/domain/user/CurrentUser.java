package com.backstage.system.domain.user;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/7
 * Time: 16:53
 */
public class CurrentUser extends OshUser {
    private List<String> role;
    private List<String> permissionList;

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
        return "CurrentUser{" +
                "role=" + role +
                ", permissionList=" + permissionList +
                '}';
    }
}
