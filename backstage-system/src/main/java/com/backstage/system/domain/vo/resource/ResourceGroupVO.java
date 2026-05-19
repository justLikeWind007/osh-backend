package com.backstage.system.domain.vo.resource;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源组返回 VO
 *
 * @author backstage
 */
public class ResourceGroupVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资源组ID */
    private Long id;

    /** 资源组名称 */
    private String name;

    /** 资源组描述 */
    private String description;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /** 关联的资源列表 */
    private List<ResourceVO> resources;

    /** 关联的链接列表 */
    private List<ResourceLinkVO> links;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public List<ResourceVO> getResources() {
        return resources;
    }

    public void setResources(List<ResourceVO> resources) {
        this.resources = resources;
    }

    public List<ResourceLinkVO> getLinks() {
        return links;
    }

    public void setLinks(List<ResourceLinkVO> links) {
        this.links = links;
    }
}
