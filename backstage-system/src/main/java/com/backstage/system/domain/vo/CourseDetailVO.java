package com.backstage.system.domain.vo;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程详情响应 VO
 * 
 * @author ruoyi
 * @date 2026-01-XX
 */
@ApiModel(description = "课程详情响应")
public class CourseDetailVO implements Serializable
{
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

    /** 课程介绍/试看内容 */
    @ApiModelProperty("课程介绍")
    private String tryContent;

    /** 当前价格 */
    @ApiModelProperty("当前价格")
    private String price;

    /** 原价/市场价 */
    @ApiModelProperty("原价/市场价")
    private String tPrice;

    /** 课程类型：media-视频课，live-直播课，text-图文课 */
    @ApiModelProperty("课程类型")
    private String type;

    /** 章节数量 */
    @ApiModelProperty("章节数量")
    private Integer subCount;

    /** 是否已购买 */
    @ApiModelProperty("是否已购买")
    private Boolean isbuy;

    /** 是否已收藏 */
    @ApiModelProperty("是否已收藏")
    private Boolean isfava;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCover()
    {
        return cover;
    }

    public void setCover(String cover)
    {
        this.cover = cover;
    }

    public String getTryContent()
    {
        return tryContent;
    }

    public void setTryContent(String tryContent)
    {
        this.tryContent = tryContent;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getTPrice()
    {
        return tPrice;
    }

    public void setTPrice(String tPrice)
    {
        this.tPrice = tPrice;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getSubCount()
    {
        return subCount;
    }

    public void setSubCount(Integer subCount)
    {
        this.subCount = subCount;
    }

    public Boolean getIsbuy()
    {
        return isbuy;
    }

    public void setIsbuy(Boolean isbuy)
    {
        this.isbuy = isbuy;
    }

    public Boolean getIsfava()
    {
        return isfava;
    }

    public void setIsfava(Boolean isfava)
    {
        this.isfava = isfava;
    }
}
