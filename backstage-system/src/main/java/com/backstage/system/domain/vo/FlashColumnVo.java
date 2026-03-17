package com.backstage.system.domain.vo;

import com.backstage.common.annotation.Excel;
import com.backstage.system.domain.SysFlashSale;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.List;

public class FlashColumnVo {
    private static final long serialVersionUID = 1L;

    /** 专栏ID */
    private Long id;

    /** 专栏标题 */
    @Excel(name = "专栏标题")
    private String title;

    /** 封面图 */
    @Excel(name = "封面图")
    private String cover;

    /** 试看描述/摘要 */
    @Excel(name = "试看描述/摘要")
    private String col_try;

    /** 专栏介绍详情 */
    @Excel(name = "专栏介绍详情")
    private String content;

    /** 原价 */
    @Excel(name = "原价")
    private BigDecimal price;

    /** 划线价 */
    @Excel(name = "划线价")
    private BigDecimal tPrice;

    /** 是否完结 (0否 1是) */
    @Excel(name = "是否完结 (0否 1是)")
    private Integer isend;

    /** 订阅人数 */
    @Excel(name = "订阅人数")
    private Long subCount;

    /** 是否购买标记 */
    @Excel(name = "是否购买标记")
    private Integer buyFlag;

    /** 专栏关联的课程列表（虚拟字段，数据库表里没有） */
    private List<FlashCourseVo> columnCourses;

    // 1. 定义私有变量
    private SysFlashSale flashsale;

    /** 创建时间 */
    private java.util.Date createTime;

    // 2. 必须有对应的 Getter (用于返回数据)
    @JsonProperty("flashsale")
    public SysFlashSale getFlashsale() {
        return flashsale;
    }

    // 3. 必须有对应的 Setter (用于 Service 层赋值)
    public void setFlashsale(SysFlashSale flashsale) {
        this.flashsale = flashsale;
    }

    @JsonProperty("column_courses") // 重点：确保返回的 JSON 键名带下划线
    public List<FlashCourseVo> getColumnCourses() {
        return columnCourses;
    }

    public void setColumnCourses(List<FlashCourseVo> columnCourses) {
        this.columnCourses = columnCourses;
    }


    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime() {
        return createTime;
    }

    /** 匹配文档要求的 isbuy 字段 */
    @JsonProperty("isbuy")
    private Boolean isbuy = false;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
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

    public void setTry(String col_try)
    {
        this.col_try = col_try;
    }

    public String getTry()
    {
        return col_try;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
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

    public void setIsend(Integer isend)
    {
        this.isend = isend;
    }

    public Integer getIsend()
    {
        return isend;
    }

    public void setSubCount(Long subCount)
    {
        this.subCount = subCount;
    }

    public Long getSubCount()
    {
        return subCount;
    }

    public void setBuyFlag(Integer buyFlag)
    {
        this.buyFlag = buyFlag;
    }

    public Integer getBuyFlag()
    {
        return buyFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("title", getTitle())
                .append("cover", getCover())
                .append("try", getTry())
                .append("content", getContent())
                .append("price", getPrice())
                .append("tPrice", gettPrice())
                .append("isend", getIsend())
                .append("subCount", getSubCount())
                .append("buyFlag", getBuyFlag())
                .append("createTime", getCreateTime())
                .append("updateTime", flashsale.getUpdateTime())
                .toString();
    }
}
