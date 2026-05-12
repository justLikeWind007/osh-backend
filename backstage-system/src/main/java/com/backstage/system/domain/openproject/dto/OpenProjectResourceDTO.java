package com.backstage.system.domain.openproject.dto;

/**
 * 提交时携带的资源关联信息
 */
public class OpenProjectResourceDTO {

    /** 资源类型：course / book / tool */
    private String resourceType;

    /** 资源前端路由（如 /course/1） */
    private String resourceUrl;

    /** 资源名称（选填，方便展示） */
    private String resourceName;

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourceUrl() { return resourceUrl; }
    public void setResourceUrl(String resourceUrl) { this.resourceUrl = resourceUrl; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
}
