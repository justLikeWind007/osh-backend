package com.backstage.system.domain.vo;

import java.io.Serializable;
import java.util.List;
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

    /** 服务周期 */
    @ApiModelProperty("服务周期")
    private String serviceCycle;

    /** 服务内容 */
    @ApiModelProperty("服务内容")
    private String serviceContent;

    /** 好评数 */
    @ApiModelProperty("好评数")
    private Integer goodCount;

    /** 中评数 */
    @ApiModelProperty("中评数")
    private Integer mediumCount;

    /** 差评数 */
    @ApiModelProperty("差评数")
    private Integer badCount;

    /** 章节数量 */
    @ApiModelProperty("章节数量")
    private Integer subCount;

    /** 是否已购买 */
    @ApiModelProperty("是否已购买")
    private Boolean isBuy;

    /** 是否已收藏 */
    @ApiModelProperty("是否已收藏")
    private Boolean isfava;

    /** 章节列表 */
    @ApiModelProperty("章节列表")
    private List<CourseSectionVO> sections;

    /** 资源类型 */
    @ApiModelProperty("资源类型")
    private String resourceType;

    // 如果类上没有 @Data 注解，务必手动加上 Setter
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }

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

    public String getServiceCycle()
    {
        return serviceCycle;
    }

    public void setServiceCycle(String serviceCycle)
    {
        this.serviceCycle = serviceCycle;
    }

    public String getServiceContent()
    {
        return serviceContent;
    }

    public void setServiceContent(String serviceContent)
    {
        this.serviceContent = serviceContent;
    }

    public Integer getGoodCount()
    {
        return goodCount;
    }

    public void setGoodCount(Integer goodCount)
    {
        this.goodCount = goodCount;
    }

    public Integer getMediumCount()
    {
        return mediumCount;
    }

    public void setMediumCount(Integer mediumCount)
    {
        this.mediumCount = mediumCount;
    }

    public Integer getBadCount()
    {
        return badCount;
    }

    public void setBadCount(Integer badCount)
    {
        this.badCount = badCount;
    }

    public Integer getSubCount()
    {
        return subCount;
    }

    public void setSubCount(Integer subCount)
    {
        this.subCount = subCount;
    }

    public Boolean getIsBuy()
    {
        return isBuy;
    }

    public void setIsBuy(Boolean isBuy)
    {
        this.isBuy = isBuy;
    }

    public Boolean getIsfava()
    {
        return isfava;
    }

    public void setIsfava(Boolean isfava)
    {
        this.isfava = isfava;
    }

    public List<CourseSectionVO> getSections()
    {
        return sections;
    }

    public void setSections(List<CourseSectionVO> sections)
    {
        this.sections = sections;
    }
}
