package com.backstage.system.domain.openproject.vo;

/**
 * 开源项目排行榜条目
 */
public class OpenProjectRankVO {

    /** 项目ID */
    private Long id;

    /** 项目名称 */
    private String projectName;

    /** 项目描述 */
    private String projectDesc;

    /** 项目链接 */
    private String projectUrl;

    /** 封面图片 */
    private String projectCover;

    /** 当前 Star 数 */
    private Integer starCount;

    /** 当前 Fork 数 */
    private Integer forkCount;

    /** 周期内 Star 增量 */
    private Integer starIncrement;

    /** 周期内 Fork 增量 */
    private Integer forkIncrement;

    /** 排名 */
    private Integer rank;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectDesc() { return projectDesc; }
    public void setProjectDesc(String projectDesc) { this.projectDesc = projectDesc; }

    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }

    public String getProjectCover() { return projectCover; }
    public void setProjectCover(String projectCover) { this.projectCover = projectCover; }

    public Integer getStarCount() { return starCount; }
    public void setStarCount(Integer starCount) { this.starCount = starCount; }

    public Integer getForkCount() { return forkCount; }
    public void setForkCount(Integer forkCount) { this.forkCount = forkCount; }

    public Integer getStarIncrement() { return starIncrement; }
    public void setStarIncrement(Integer starIncrement) { this.starIncrement = starIncrement; }

    public Integer getForkIncrement() { return forkIncrement; }
    public void setForkIncrement(Integer forkIncrement) { this.forkIncrement = forkIncrement; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
}
