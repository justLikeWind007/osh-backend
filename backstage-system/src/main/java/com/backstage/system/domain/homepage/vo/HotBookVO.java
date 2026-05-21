package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首页热门电子书 VO
 *
 * @author jayTatum
 */
@ApiModel(description = "首页热门电子书")
public class HotBookVO {

    @ApiModelProperty("电子书ID")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("封面URL")
    private String cover;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("当前价格")
    private BigDecimal price;

    @ApiModelProperty("订阅数(购买人数)")
    private Integer subCount;

    @ApiModelProperty("等级")
    private Integer level;

    @ApiModelProperty("章节总数")
    private Integer chapterCount;

    @ApiModelProperty("热度分")
    private Double hotScore;

    @ApiModelProperty("详情页跳转路径")
    private String detailUrl;

    @ApiModelProperty("标签列表(最多2个)")
    private List<String> tags;

    // 标签查询用临时字段
    @ApiModelProperty(hidden = true)
    private Long bookId;

    @ApiModelProperty(hidden = true)
    private String tagName;

    // ========== getter / setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getSubCount() { return subCount; }
    public void setSubCount(Integer subCount) { this.subCount = subCount; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Integer getChapterCount() { return chapterCount; }
    public void setChapterCount(Integer chapterCount) { this.chapterCount = chapterCount; }

    public Double getHotScore() { return hotScore; }
    public void setHotScore(Double hotScore) { this.hotScore = hotScore; }

    public String getDetailUrl() { return detailUrl; }
    public void setDetailUrl(String detailUrl) { this.detailUrl = detailUrl; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }
}
