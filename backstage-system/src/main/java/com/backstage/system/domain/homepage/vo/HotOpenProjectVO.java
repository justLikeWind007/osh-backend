package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 首页热门开源项目 VO
 *
 * @author jayTatum
 */
@ApiModel(description = "首页热门开源项目")
public class HotOpenProjectVO {

    @ApiModelProperty("项目ID")
    private Long id;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("项目描述")
    private String projectDesc;

    @ApiModelProperty("项目链接")
    private String projectUrl;

    @ApiModelProperty("作者名称")
    private String authorName;

    @ApiModelProperty("封面图URL")
    private String projectCover;

    @ApiModelProperty("Star数")
    private Integer starCount;

    @ApiModelProperty("Fork数")
    private Integer forkCount;

    @ApiModelProperty("最近提交时间（格式化字符串）")
    private String lastCommit;

    @ApiModelProperty("标签列表")
    private List<String> tagList;

    @ApiModelProperty("详情页跳转路径")
    private String detailUrl;

    // 标签查询用临时字段
    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private String tagName;

    // ========== getter / setter ==========

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

    public Integer getStarCount() { return starCount; }
    public void setStarCount(Integer starCount) { this.starCount = starCount; }

    public Integer getForkCount() { return forkCount; }
    public void setForkCount(Integer forkCount) { this.forkCount = forkCount; }

    public String getLastCommit() { return lastCommit; }
    public void setLastCommit(String lastCommit) { this.lastCommit = lastCommit; }

    public List<String> getTagList() { return tagList; }
    public void setTagList(List<String> tagList) { this.tagList = tagList; }

    public String getDetailUrl() { return detailUrl; }
    public void setDetailUrl(String detailUrl) { this.detailUrl = detailUrl; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }
}
