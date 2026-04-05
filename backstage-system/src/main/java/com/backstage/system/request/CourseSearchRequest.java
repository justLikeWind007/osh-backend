package com.backstage.system.request;

import com.backstage.common.request.PageRequest;

import java.util.List;

/**
 * @Author: Hope_Liu
 * @createTime: 2026年03月30日 21:56:42
 * @version:
 * @Description:
 */
public class CourseSearchRequest extends PageRequest {

    private List<String> tags;
    private String keyword;

    public CourseSearchRequest() {
    }

    public CourseSearchRequest(List<String> tags, String keyword) {
        this.tags = tags;
        this.keyword = keyword;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
