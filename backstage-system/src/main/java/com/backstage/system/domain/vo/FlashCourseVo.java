package com.backstage.system.domain.vo;

import com.backstage.system.domain.SysFlashSale;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 课程前端展示对象 (减少冗余字段，提升传输效率)
 */
@ApiModel(description = "课程列表展示对象")
public class FlashCourseVo {

    @ApiModelProperty("课程 ID")
    private Long id;

    @ApiModelProperty("课程标题")
    private String title;

    @ApiModelProperty("课程封面图 URL")
    private String cover;

    @ApiModelProperty("当前价格")
    private BigDecimal price;

    @ApiModelProperty("原价/市场价")
    private BigDecimal tPrice;

    @ApiModelProperty("课程类型")
    private String type;

    @ApiModelProperty("秒杀活动详情")
    private SysFlashSale flashsale;

    private String courseType;


    private String tryText;

    private BigDecimal oldPrice;

    private Date createTime;

    private Integer countNum;

    private String buyFlag;

    private String favaFlag;


    public String getFavaFlag() {
        return favaFlag;
    }

    public void setFavaFlag(String favaFlag) {
        this.favaFlag = favaFlag;
    }

    public String getBuyFlag() {
        return buyFlag;
    }

    public void setBuyFlag(String buyFlag) {
        this.buyFlag = buyFlag;
    }

    public Integer getCountNum() {
        return countNum;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }



    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public void setTryText(String tryText) {
        this.tryText = tryText;
    }

    public String getTryText() {
        return tryText;
    }


    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }
    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCourseType() {
        return courseType;
    }



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getTPrice() { return tPrice; }
    public void setTPrice(BigDecimal tPrice) { this.tPrice = tPrice; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public SysFlashSale getFlashsale() { return flashsale; }
    public void setFlashsale(SysFlashSale flashsale) { this.flashsale = flashsale; }
}