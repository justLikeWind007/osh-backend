package com.backstage.system.domain.common;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 推荐列内容对象 osh_recommend_courses
 * 
 * @author ruoyi
 * @date 2026-03-10
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OshRecommendCourses extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 内容ID */
    private String id;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 封面图片URL */
    @Excel(name = "封面图片URL")
    private String cover;

    /** 原价 */
    @Excel(name = "原价")
    private BigDecimal price;

    /** 优惠价 */
    @Excel(name = "优惠价")
    @JsonProperty("t_price")
    private BigDecimal tPrice;

    /** 内容类型 */
    @Excel(name = "内容类型")
    private String type;

    /** 订阅数 */
    @Excel(name = "订阅数")
    @JsonProperty("sub_count")
    private String subCount;

    /** 是否显示 1=是 0=否 */
    @Excel(name = "是否显示 1=是 0=否")
    private Integer isShow;

    /** 排序权重 */
    @Excel(name = "排序权重")
    private String sort;

    /** 内容描述 */
    @Excel(name = "内容描述")
    private String description;

    /** 原始链接 */
    @Excel(name = "原始链接")
    private String originalUrl;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setCover(String cover) 
    {
        this.cover = cover;
    }

    public String getCover() 
    {
        return cover;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void settPrice(BigDecimal tPrice) 
    {
        this.tPrice = tPrice;
    }

    public BigDecimal gettPrice() 
    {
        return tPrice;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setSubCount(String subCount) 
    {
        this.subCount = subCount;
    }

    public String getSubCount() 
    {
        return subCount;
    }

    public void setIsShow(Integer isShow) 
    {
        this.isShow = isShow;
    }

    public Integer getIsShow() 
    {
        return isShow;
    }

    public void setSort(String sort) 
    {
        this.sort = sort;
    }

    public String getSort() 
    {
        return sort;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setOriginalUrl(String originalUrl) 
    {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() 
    {
        return originalUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("cover", getCover())
            .append("price", getPrice())
            .append("tPrice", gettPrice())
            .append("type", getType())
            .append("subCount", getSubCount())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("isShow", getIsShow())
            .append("sort", getSort())
            .append("description", getDescription())
            .append("originalUrl", getOriginalUrl())
            .toString();
    }
}
