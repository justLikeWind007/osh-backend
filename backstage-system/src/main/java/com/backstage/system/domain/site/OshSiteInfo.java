package com.backstage.system.domain.site;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 内部网站信息对象 osh_site_info
 *
 * @author backstage
 */
@TableName("osh_site_info")
public class OshSiteInfo implements Serializable {

    private static final long serialVersionUID = 4936551355109148940L;

    /**
     * 网站编号（主键）
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id", updateStrategy = FieldStrategy.NEVER)
    private Long id;

    /**
     * 网站名称
     */
    @NotBlank(message = "请输入网站名称")
    @TableField(value = "site_name", updateStrategy = FieldStrategy.NOT_EMPTY)
    private String siteName;

    /**
     * 网站封面地址
     */
    @TableField(value = "cover", updateStrategy = FieldStrategy.NOT_EMPTY)
    private String cover;

    /**
     * 网站访问路径
     */
    @NotBlank(message = "请输入网站访问路径")
    @TableField(value = "site_url", updateStrategy = FieldStrategy.NOT_EMPTY)
    private String siteUrl;

    /**
     * 网站描述信息
     */
    @TableField(value = "description", updateStrategy = FieldStrategy.NOT_EMPTY)
    private String description;

    /**
     * 标签（多个标签用逗号分隔）
     */
    @TableField(exist = false)
    private List<OshSiteTag> tagList;

    /**
     * 状态
     */
    @TableField(value = "status", updateStrategy = FieldStrategy.NOT_NULL)
    private Integer status;

    /**
     * 最后检查时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "last_check_time")
    private Date lastCheckTime;

    /**
     * 最后检查状态
     */
    @TableField(value = "last_check_status")
    private Integer lastCheckStatus;

    /**
     * 最后检查状态名称
     */
    @TableField(exist = false)
    private String lastCheckStatusName;

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
    private Date updateTime;

    /**
     * 是否删除：0=未删除，1=已删除
     */
    @TableLogic
    @TableField(value = "is_deleted")
    private Integer isDeleted;

    /**
     * 维护者
     */
    @TableField(exist = false)
    private List<OshSiteMaintainer> maintainers;

    /**
     * 维护者用户ID
     */
    @TableField(exist = false)
    private List<String> maintainerUserIds;

    public List<String> getMaintainerUserIds() {
        return maintainerUserIds;
    }

    public void setMaintainerUserIds(List<String> maintainerUserIds) {
        this.maintainerUserIds = maintainerUserIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OshSiteTag> getTagList() {
        return tagList;
    }

    public void setTagList(List<OshSiteTag> tagList) {
        this.tagList = tagList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
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

    public List<OshSiteMaintainer> getMaintainers() {
        return maintainers;
    }

    public void setMaintainers(List<OshSiteMaintainer> maintainers) {
        this.maintainers = maintainers;
    }

    public Integer getLastCheckStatus() {
        return lastCheckStatus;
    }

    public void setLastCheckStatus(Integer lastCheckStatus) {
        this.lastCheckStatus = lastCheckStatus;
    }

    public String getLastCheckStatusName() {
        return lastCheckStatusName;
    }

    public void setLastCheckStatusName(String lastCheckStatusName) {
        this.lastCheckStatusName = lastCheckStatusName;
    }
}
