package com.backstage.system.domain.site;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 网站负责人对象 osh_site_maintainer
 *
 * @author backstage
 */
@TableName("osh_site_maintainer")
public class OshSiteMaintainer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id", updateStrategy = FieldStrategy.NEVER)
    private Long id;

    /**
     * 关联网站ID
     */
    @NotNull(message = "网站ID不能为空")
    @TableField(value = "site_id", updateStrategy = FieldStrategy.NOT_NULL)
    private Long siteId;

    /**
     * 负责人用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @TableField(value = "user_id", updateStrategy = FieldStrategy.NOT_NULL)
    private Long userId;

    /**
     * 负责人姓名
     */
    @NotBlank(message = "负责人姓名不能为空")
    @TableField(value = "user_name", updateStrategy = FieldStrategy.NOT_EMPTY)
    private String userName;

    /**
     * 创建人ID/账号
     */
    @TableField(value = "created_by", updateStrategy = FieldStrategy.NEVER)
    private Long createdBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", updateStrategy = FieldStrategy.NEVER)
    private Date creationTime;

    /**
     * 更新人ID/账号
     */
    @TableField(value = "update_by")
    private Long updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除：0=未删除，1=已删除
     */
    @TableLogic
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
