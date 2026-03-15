package com.backstage.system.domain.dto;

import java.io.Serializable;

public class SearchQueryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String keyword; // 搜索关键词
    private String type;    // course 或 column
    private Integer page;   // 页码

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    // 建议顺手加个 toString，方便后面调试看日志
    @Override
    public String toString() {
        return "SearchQueryDto{" +
                "keyword='" + keyword + '\'' +
                ", type='" + type + '\'' +
                ", page=" + page +
                '}';
    }
}