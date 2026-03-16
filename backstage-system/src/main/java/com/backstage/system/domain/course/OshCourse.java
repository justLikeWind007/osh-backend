package com.backstage.system.domain.course;

import com.backstage.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 课程信息对象 osh_coures
 * 
 * @author ruoyi
 * @date 2026-01-XX
 */
@ApiModel(description = "课程信息")

public class OshCourse

{
    private static final long serialVersionUID = 1L;

    /** 课程 ID */
    @ApiModelProperty("课程 ID")
    private Long id;

    /** 课程标题 */
    @Excel(name = "课程标题")
    @ApiModelProperty("课程标题")
    private String title;

    /** 课程封面图 URL */
    @Excel(name = "课程封面图 URL")
    @ApiModelProperty("课程封面图 URL")
    private String cover;

    /** 课程介绍/试看内容 */
    @Excel(name = "课程介绍")
    @ApiModelProperty("课程介绍/试看内容")
    private String tryContent;

    /** 当前价格 */
    @Excel(name = "当前价格")
    @ApiModelProperty("当前价格")
    private BigDecimal price;

    /** 原价/市场价 */
    @Excel(name = "原价")
    @ApiModelProperty("原价/市场价")
    private BigDecimal tPrice;

    /** 课程类型：media-视频课，live-直播课，text-图文课 */
    @Excel(name = "课程类型")
    @ApiModelProperty("课程类型：media-视频课，live-直播课，text-图文课")
    private String type;

//
//    /** 章节数量 */
//    @Excel(name = "章节数量")
//    @ApiModelProperty("章节数量")
//    private Integer subCount;
//
//    /** 所属专栏 ID */
//    @Excel(name = "专栏 ID")
//    @ApiModelProperty("所属专栏 ID")
//    private Long columnId;
//
//    /** 网校 appid */
//    @Excel(name = "网校 appid")
//    @ApiModelProperty("网校 appid")
//    private String appid;
//
//    /** 是否已购买：0-否，1-是 */
//    @Excel(name = "是否已购买", readConverterExp = "0=否，1=是")
//    @ApiModelProperty("是否已购买：0-否，1-是")
//    private Boolean isbuy;
//
//    /** 是否已收藏：0-否，1-是 */
//    @Excel(name = "是否已收藏", readConverterExp = "0=否，1=是")
//    @ApiModelProperty("是否已收藏：0-否，1-是")
//    private Boolean isfava;
//
//    /** 备用字段 1 */
//    @ApiModelProperty("备用字段 1")
//    private String extField1;
//
//    /** 备用字段 2 */
//    @ApiModelProperty("备用字段 2")
//    private String extField2;
//
//    /** 备用字段 3 */
//    @ApiModelProperty("备用字段 3")
//    private String extField3;
//
//    /** 备用字段 4 */
//    @ApiModelProperty("备用字段 4")
//    private String extField4;
//
//    /** 备用字段 5 */
//    @ApiModelProperty("备用字段 5")
//    private Integer extField5;
//
//    /** 备用字段 6 */
//    @ApiModelProperty("备用字段 6")
//    private Integer extField6;
//
//    /** 备用字段 7 */
//    @ApiModelProperty("备用字段 7")
//    private BigDecimal extField7;
//
//    /** 备用字段 8 */
//    @ApiModelProperty("备用字段 8")
//    private Date extField8;


    /** 搜索值 */
    private String searchValue;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

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

    public void setTryContent(String tryContent) 
    {
        this.tryContent = tryContent;
    }

    public String getTryContent() 
    {
        return tryContent;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setTPrice(BigDecimal tPrice) 
    {
        this.tPrice = tPrice;
    }

    public BigDecimal getTPrice() 
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

//    public void setSubCount(Integer subCount)
//    {
//        this.subCount = subCount;
//    }
//
//    public Integer getSubCount()
//    {
//        return subCount;
//    }
//
//    public void setColumnId(Long columnId)
//    {
//        this.columnId = columnId;
//    }
//
//    public Long getColumnId()
//    {
//        return columnId;
//    }
//
//    public void setAppid(String appid)
//    {
//        this.appid = appid;
//    }
//
//    public String getAppid()
//    {
//        return appid;
//    }
//
//    public void setIsbuy(Boolean isbuy)
//    {
//        this.isbuy = isbuy;
//    }
//
//    public Boolean getIsbuy()
//    {
//        return isbuy;
//    }
//
//    public void setIsfava(Boolean isfava)
//    {
//        this.isfava = isfava;
//    }
//
//    public Boolean getIsfava()
//    {
//        return isfava;
//    }
//
//    public String getExtField1()
//    {
//        return extField1;
//    }
//
//    public void setExtField1(String extField1)
//    {
//        this.extField1 = extField1;
//    }
//
//    public String getExtField2()
//    {
//        return extField2;
//    }
//
//    public void setExtField2(String extField2)
//    {
//        this.extField2 = extField2;
//    }
//
//    public String getExtField3()
//    {
//        return extField3;
//    }
//
//    public void setExtField3(String extField3)
//    {
//        this.extField3 = extField3;
//    }
//
//    public String getExtField4()
//    {
//        return extField4;
//    }
//
//    public void setExtField4(String extField4)
//    {
//        this.extField4 = extField4;
//    }
//
//    public Integer getExtField5()
//    {
//        return extField5;
//    }
//
//    public void setExtField5(Integer extField5)
//    {
//        this.extField5 = extField5;
//    }
//
//    public Integer getExtField6()
//    {
//        return extField6;
//    }
//
//    public void setExtField6(Integer extField6)
//    {
//        this.extField6 = extField6;
//    }
//
//    public BigDecimal getExtField7()
//    {
//        return extField7;
//    }
//
//    public void setExtField7(BigDecimal extField7)
//    {
//        this.extField7 = extField7;
//    }
//
//    public Date getExtField8()
//    {
//        return extField8;
//    }
//
//    public void setExtField8(Date extField8)
//    {
//        this.extField8 = extField8;
//    }

    public String getSearchValue()
    {
        return searchValue;
    }

    public void setSearchValue(String searchValue)
    {
        this.searchValue = searchValue;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public String getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("cover", getCover())
            .append("tryContent", getTryContent())
            .append("price", getPrice())
            .append("tPrice", getTPrice())
            .append("type", getType())
//            .append("subCount", getSubCount())
//            .append("columnId", getColumnId())
//            .append("appid", getAppid())
//            .append("isbuy", getIsbuy())
//            .append("isfava", getIsfava())
//            .append("extField1", getExtField1())
//            .append("extField2", getExtField2())
//            .append("extField3", getExtField3())
//            .append("extField4", getExtField4())
//            .append("extField5", getExtField5())
//            .append("extField6", getExtField6())
//            .append("extField7", getExtField7())
//            .append("extField8", getExtField8())
            .append("searchValue", getSearchValue())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
