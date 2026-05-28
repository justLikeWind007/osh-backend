package com.backstage.system.domain.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户管理 - 列表项返回
 */
public class UserManageItemVO {

    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String sex;
    private String introduction;
    private Integer status;
    private Integer violationCount;
    private String inviteCode;
    private Integer deleteFlag;

    /** 角色名称 */
    private String roleName;
    /** 角色编码 */
    private String roleCode;
    /** 角色等级 */
    private Integer roleLevel;

    /** 用户积分 */
    private Long points;

    /** 用户所有角色列表（按等级降序） */
    private List<Map<String, Object>> roles;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getViolationCount() { return violationCount; }
    public void setViolationCount(Integer violationCount) { this.violationCount = violationCount; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    public Integer getRoleLevel() { return roleLevel; }
    public void setRoleLevel(Integer roleLevel) { this.roleLevel = roleLevel; }

    public Long getPoints() { return points; }
    public void setPoints(Long points) { this.points = points; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public List<Map<String, Object>> getRoles() { return roles; }
    public void setRoles(List<Map<String, Object>> roles) { this.roles = roles; }
}
