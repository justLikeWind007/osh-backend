package com.backstage.system.domain.vo.resource;

import java.io.Serializable;

/**
 * 资源组列表查询请求 VO（游标分页，用于无限滚动加载）
 *
 * @author backstage
 */
public class ResourceGroupListReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 关键词（按名称模糊匹配） */
    private String keyword;

    /** 游标：上一页最后一条记录的ID，首次加载传空 */
    private Long lastId;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public Integer getPageSize() {
        return pageSize == null || pageSize <= 0 ? 10 : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
