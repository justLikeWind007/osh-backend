package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 首页热门实用网站 VO
 *
 * @author jayTatum
 */
@ApiModel(description = "首页热门实用网站")
public class HotWebsiteVO {

    @ApiModelProperty("网站ID")
    private Long id;

    @ApiModelProperty("网站名称")
    private String name;

    @ApiModelProperty("网站链接")
    private String url;

    @ApiModelProperty("网站描述")
    private String description;

    @ApiModelProperty("Logo地址")
    private String logoUrl;

    @ApiModelProperty("点击次数")
    private Integer clickCount;

    @ApiModelProperty("好评数")
    private Integer goodCount;

    @ApiModelProperty("收藏数")
    private Integer collectionCount;

    @ApiModelProperty("详情页跳转路径")
    private String detailUrl;

    // ========== getter / setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public Integer getClickCount() { return clickCount; }
    public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }

    public Integer getGoodCount() { return goodCount; }
    public void setGoodCount(Integer goodCount) { this.goodCount = goodCount; }

    public Integer getCollectionCount() { return collectionCount; }
    public void setCollectionCount(Integer collectionCount) { this.collectionCount = collectionCount; }

    public String getDetailUrl() { return detailUrl; }
    public void setDetailUrl(String detailUrl) { this.detailUrl = detailUrl; }
}
