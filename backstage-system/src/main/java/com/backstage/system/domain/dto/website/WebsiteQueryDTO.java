package com.backstage.system.domain.dto.website;

import java.io.Serializable;
import java.util.List;

/**
 * 实用网站查询参数对象
 */
public class WebsiteQueryDTO implements Serializable {
    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;
    /** 网站名称（模糊查询） */
    private String websiteName;

    /** 标签名称（多个标签用逗号分隔，如："后端标签,AI 标签"） */
    private List<String> tagNames;

    /** 标签数量（用于 HAVING 子句） */
    private Integer tagNameCount;

    /** 当前页码 */
    private Integer pageNum = 1;

    /** 每页大小 */
    private Integer pageSize = 10;

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
        if (tagNames != null && !tagNames.isEmpty()) {
            this.tagNameCount = tagNames.size();
        } else {
            this.tagNameCount = 0;
        }
    }

    public Integer getTagNameCount() {
        return tagNameCount;
    }

    public void setTagNameCount(Integer tagNameCount) {
        this.tagNameCount = tagNameCount;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "WebsiteQueryDTO{" +
                "websiteName='" + websiteName + '\'' +
                ", tagNames=" + tagNames +
                ", tagNameCount=" + tagNameCount +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
