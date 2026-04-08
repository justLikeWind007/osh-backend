package com.backstage.system.domain.user.vo;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/7
 * Time: 18:21
 */
public class OshRoleVO {
    private String roleName;
    private String roleCode;
    private Integer level;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "OshRoleVO{" +
                "roleName='" + roleName + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", level=" + level +
                '}';
    }
}
