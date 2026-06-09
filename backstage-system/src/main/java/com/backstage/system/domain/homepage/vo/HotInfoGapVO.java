package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 首页热门信息差 VO
 *
 * @author jayTatum
 */
@ApiModel(description = "首页热门信息差")
public class HotInfoGapVO {

    @ApiModelProperty("信息差ID")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("标签（逗号分隔）")
    private String tag;

    @ApiModelProperty("内容摘要")
    private String content;

    @ApiModelProperty("好评数")
    private Integer goodCount;

    @ApiModelProperty("中评数")
    private Integer middleCount;

    @ApiModelProperty("差评数")
    private Integer badCount;

    @ApiModelProperty("发布者用户名")
    private String userName;

    @ApiModelProperty("详情页跳转路径")
    private String detailUrl;

    // ========== getter / setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getGoodCount() { return goodCount; }
    public void setGoodCount(Integer goodCount) { this.goodCount = goodCount; }

    public Integer getMiddleCount() { return middleCount; }
    public void setMiddleCount(Integer middleCount) { this.middleCount = middleCount; }

    public Integer getBadCount() { return badCount; }
    public void setBadCount(Integer badCount) { this.badCount = badCount; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getDetailUrl() { return detailUrl; }
    public void setDetailUrl(String detailUrl) { this.detailUrl = detailUrl; }
}
