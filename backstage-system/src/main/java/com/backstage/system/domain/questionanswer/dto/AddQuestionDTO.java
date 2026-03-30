package com.backstage.system.domain.questionanswer.dto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/28
 * Time: 21:12
 */
public class AddQuestionDTO {
    private String resourceType;
    private Long resourceNo;
    private String title;
    private String content;
    private Integer isPaidOnly;
    private List<Long> tags;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(Long resourceNo) {
        this.resourceNo = resourceNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIsPaidOnly() {
        return isPaidOnly;
    }

    public void setIsPaidOnly(Integer isPaidOnly) {
        this.isPaidOnly = isPaidOnly;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "AddQuestionDTO{" +
                "resourceType='" + resourceType + '\'' +
                ", resourceId=" + resourceNo +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isPaidOnly=" + isPaidOnly +
                ", tags=" + tags +
                '}';
    }
}