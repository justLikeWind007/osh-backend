package com.backstage.system.domain.openproject.dto;

import java.util.List;

public class OpenProjectSubmitDTO {
    private String projectName;
    private String projectDesc;
    private String projectUrl;
    private String authorName;
    private String projectCover;

    /** 已有标签的 ID 列表 */
    private List<Long> tagIds;

    /** 用户自定义的新标签名列表（不在现有标签库中） */
    private List<String> customTags;

    /**
     * 关联课程 URL（本站课程地址，如 /course/1）
     * 若本站有对该项目的讲解课程则填写，否则传 null
     */
    private String courseUrl;

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectDesc() { return projectDesc; }
    public void setProjectDesc(String projectDesc) { this.projectDesc = projectDesc; }

    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getProjectCover() { return projectCover; }
    public void setProjectCover(String projectCover) { this.projectCover = projectCover; }

    public List<Long> getTagIds() { return tagIds; }
    public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }

    public List<String> getCustomTags() { return customTags; }
    public void setCustomTags(List<String> customTags) { this.customTags = customTags; }

    public String getCourseUrl() { return courseUrl; }
    public void setCourseUrl(String courseUrl) { this.courseUrl = courseUrl; }
}
