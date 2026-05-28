package com.backstage.system.domain.vo.user;

/**
 * 用户管理 - 查询参数
 */
public class UserManageQueryVO {

    /** 搜索：用户名（模糊） */
    private String username;

    /** 搜索：邮箱（模糊） */
    private String email;

    /** 搜索：角色ID */
    private Integer roleId;

    /** 筛选：是否被拉黑（status=1） true=已拉黑 */
    private Boolean blocked;

    /** 筛选：是否注销（delete_flag=1） true=已注销 */
    private Boolean deleted;

    /** 排序字段：createTime / points / violationCount */
    private String orderBy;

    /** 排序方向：asc / desc */
    private String orderDir;

    /** 页码 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 20;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

    public String getOrderBy() { return orderBy; }
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }

    public String getOrderDir() { return orderDir; }
    public void setOrderDir(String orderDir) { this.orderDir = orderDir; }

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
