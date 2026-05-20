package com.backstage.system.request;

import com.backstage.common.request.PageRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @Author: Hope_Lau
 * @createTime: 2026年03月30日 21:56:42
 * @version:
 * @Description:
 */
public class CourseSearchRequest extends PageRequest {

    private List<String> tags;
    private String keyword;
    private String resourceType;
    private Boolean isFollowing;
    private Integer collectionFlag;
    /**
     * Internal switch set by backend controller based on permissions.
     * true: include all statuses; false/null: only published.
     */
    private Boolean includeUnpublished;

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
        this.keyword = StringUtils.trimToNull(keyword);
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = StringUtils.trimToNull(resourceType);
    }

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public Integer getCollectionFlag() {
        return collectionFlag;
    }

    public void setCollectionFlag(Integer collectionFlag) {
        this.collectionFlag = collectionFlag;
    }

    public Boolean getIncludeUnpublished() {
        return includeUnpublished;
    }

    public void setIncludeUnpublished(Boolean includeUnpublished) {
        this.includeUnpublished = includeUnpublished;
    }
}
