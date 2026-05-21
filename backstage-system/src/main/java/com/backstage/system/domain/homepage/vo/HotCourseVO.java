package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首页热门课程 VO（包含课程信息 + 标签）
 *
 * @author jayTatum
 */
@ApiModel(description = "首页热门课程")
public class HotCourseVO {

    @ApiModelProperty("课程ID")
    private Long id;

    @ApiModelProperty("课程标题")
    private String title;

    @ApiModelProperty("课程封面图URL")
    private String cover;

    @ApiModelProperty("当前价格")
    private BigDecimal price;

    @ApiModelProperty("原价/市场价")
    private BigDecimal tPrice;

    @ApiModelProperty("购买人数")
    private Integer buyCount;

    @ApiModelProperty("浏览数")
    private Long viewCount;

    @ApiModelProperty("视频总数")
    private Integer videoCount;

    @ApiModelProperty("平均评分")
    private BigDecimal ratingScore;

    @ApiModelProperty("课程服务内容")
    private String serviceContent;

    @ApiModelProperty("资源类型：1-FREE 2-CASH_ONLY 3-CASH_POINT 4-VIP 5-SMALL_CLASS 6-INTERNAL")
    private String resourceType;

    @ApiModelProperty("热度分")
    private Double hotScore;

    @ApiModelProperty("标签列表(最多2个)")
    private List<String> tags;

    @ApiModelProperty("详情页跳转路径")
    private String detailUrl;

    // 标签查询用的临时字段（不序列化到前端）
    @ApiModelProperty(hidden = true)
    private Long courseId;

    @ApiModelProperty(hidden = true)
    private String tagName;

    // ========== getter / setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal gettPrice() { return tPrice; }
    public void settPrice(BigDecimal tPrice) { this.tPrice = tPrice; }

    public Integer getBuyCount() { return buyCount; }
    public void setBuyCount(Integer buyCount) { this.buyCount = buyCount; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public Integer getVideoCount() { return videoCount; }
    public void setVideoCount(Integer videoCount) { this.videoCount = videoCount; }

    public BigDecimal getRatingScore() { return ratingScore; }
    public void setRatingScore(BigDecimal ratingScore) { this.ratingScore = ratingScore; }

    public String getServiceContent() { return serviceContent; }
    public void setServiceContent(String serviceContent) { this.serviceContent = serviceContent; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public Double getHotScore() { return hotScore; }
    public void setHotScore(Double hotScore) { this.hotScore = hotScore; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getDetailUrl() { return detailUrl; }
    public void setDetailUrl(String detailUrl) { this.detailUrl = detailUrl; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }
}
