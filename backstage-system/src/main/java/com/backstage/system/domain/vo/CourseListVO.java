package com.backstage.system.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程列表响应 VO
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@ApiModel(description = "课程列表响应")
public class CourseListVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /** 课程 ID */
    @ApiModelProperty("课程 ID")
    private Long id;

    /** 课程标题 */
    @ApiModelProperty("课程标题")
    private String title;

    /** 课程封面图 URL */
    @ApiModelProperty("课程封面图 URL")
    private String cover;

    /** 当前价格 */
    @ApiModelProperty("当前价格")
    private BigDecimal price;

    /** 原价/市场价 */
    @ApiModelProperty("原价/市场价")
    private BigDecimal tPrice;

    /** 课程类型：media-视频课，live-直播课，text-图文课 */
    @ApiModelProperty("课程类型")
    private String type;

    /** 课程标签列表 */
    @ApiModelProperty("课程标签列表")
    private List<TagVO> tags;

    /** 好评数 */
    @ApiModelProperty("好评数")
    private Integer goodCount;

    /** 中评数 */
    @ApiModelProperty("中评数")
    private Integer midCount;

    /** 差评数 */
    @ApiModelProperty("差评数")
    private Integer badCount;

    /** 收藏数 */
    @ApiModelProperty("收藏数")
    private Integer favaCount;

    /** 购买数 */
    @ApiModelProperty("购买数")
    private Integer buyCount;

    /** 章节数量 */
    @ApiModelProperty("章节数量")
    private Integer subCount;

    /** 免费试看章节数 */
    @ApiModelProperty("免费试看章节数")
    private Integer freeLessonCount;

    /** 当前用户是否已收藏 */
    @ApiModelProperty("当前用户是否已收藏")
    private Boolean isfava;

    /** 当前用户是否已购买 */
    @ApiModelProperty("当前用户是否已购买")
    private Boolean isbuy;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTPrice() {
        return tPrice;
    }

    public void setTPrice(BigDecimal tPrice) {
        this.tPrice = tPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TagVO> getTags() {
        return tags;
    }

    public void setTags(List<TagVO> tags) {
        this.tags = tags;
    }

    public Integer getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(Integer goodCount) {
        this.goodCount = goodCount;
    }

    public Integer getMidCount() {
        return midCount;
    }

    public void setMidCount(Integer midCount) {
        this.midCount = midCount;
    }

    public Integer getBadCount() {
        return badCount;
    }

    public void setBadCount(Integer badCount) {
        this.badCount = badCount;
    }

    public Integer getFavaCount() {
        return favaCount;
    }

    public void setFavaCount(Integer favaCount) {
        this.favaCount = favaCount;
    }

    public Integer getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Integer buyCount) {
        this.buyCount = buyCount;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
    }

    public Integer getFreeLessonCount() {
        return freeLessonCount;
    }

    public void setFreeLessonCount(Integer freeLessonCount) {
        this.freeLessonCount = freeLessonCount;
    }

    public Boolean getIsfava() {
        return isfava;
    }

    public void setIsfava(Boolean isfava) {
        this.isfava = isfava;
    }

    public Boolean getIsbuy() {
        return isbuy;
    }

    public void setIsbuy(Boolean isbuy) {
        this.isbuy = isbuy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
