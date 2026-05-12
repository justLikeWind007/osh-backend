package com.backstage.system.domain.openproject.vo;

import com.backstage.system.domain.openproject.OshOpenProjectResourceRel;

import java.time.LocalDateTime;
import java.util.List;

public class OpenProjectVO {
    private Long id;
    private String projectName;
    private String projectDesc;
    private String projectUrl;
    private String authorName;
    private String projectCover;
    private Integer status;
    private Integer clickCount;
    private LocalDateTime createTime;
    private List<Long> tagIds;
    private List<String> tagNames;

    // GitHub 同步字段
    private Integer starCount;
    private Integer forkCount;
    private LocalDateTime lastCommitTime;
    private Byte isArchived;
    private LocalDateTime lastSyncTime;

    // 关联本站资源（课程、电子书、工具等）
    private List<OshOpenProjectResourceRel> resources;

    /** 当前用户是否已收藏 */
    private Boolean favorited;

    public Boolean getFavorited() { return favorited; }
    public void setFavorited(Boolean favorited) { this.favorited = favorited; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getClickCount() { return clickCount; }
    public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public List<Long> getTagIds() { return tagIds; }
    public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }

    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }

    public Integer getStarCount() { return starCount; }
    public void setStarCount(Integer starCount) { this.starCount = starCount; }

    public Integer getForkCount() { return forkCount; }
    public void setForkCount(Integer forkCount) { this.forkCount = forkCount; }

    public LocalDateTime getLastCommitTime() { return lastCommitTime; }
    public void setLastCommitTime(LocalDateTime lastCommitTime) { this.lastCommitTime = lastCommitTime; }

    public Byte getIsArchived() { return isArchived; }
    public void setIsArchived(Byte isArchived) { this.isArchived = isArchived; }

    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }

    public List<OshOpenProjectResourceRel> getResources() { return resources; }
    public void setResources(List<OshOpenProjectResourceRel> resources) { this.resources = resources; }
}
