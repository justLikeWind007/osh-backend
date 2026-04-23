package com.backstage.system.domain.dto.website;

import java.io.Serializable;
import java.util.List;

/**
 * 实用网站提交 DTO
 * 用于接收用户提交的网站信息
 */
public class WebsiteSubmitDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 网站名称（必填） */
    private String name;

    /** 网站链接（必填） */
    private String url;

    /** 网站描述（可选） */
    private String description;

    /** 网站 Logo 地址（可选） */
    private String logoUrl;

    /** 标签名称列表（可选） */
    private List<String> tagNames;

    // Getter 和 Setter 方法

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    @Override
    public String toString() {
        return "WebsiteSubmitDTO{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                '}';
    }
}
