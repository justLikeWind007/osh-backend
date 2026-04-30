package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 课程信息 DTO
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "课程信息")
public class CourseDTO {
    
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
    @ApiModelProperty("课程介绍/试看内容")
    private String tryContent;
    
    /** 当前价格 */
    @ApiModelProperty("当前价格")
    private BigDecimal price;
    
    /** 原价/市场价 */
    @ApiModelProperty("原价/市场价")
    private BigDecimal tPrice;
    
    /** 课程类型 */
    @ApiModelProperty("课程类型：media-视频课，live-直播课，text-图文课")
    private String type;
    
    /** 服务周期 */
    @ApiModelProperty("服务周期")
    private String serviceCycle;
    
    /** 服务内容 */
    @ApiModelProperty("服务内容")
    private String serviceContent;
    
    /** 标签 ID 列表 */
    @ApiModelProperty("标签 ID 列表")
    private Long[] tagIds;

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

    public String getTryContent() {
        return tryContent;
    }

    public void setTryContent(String tryContent) {
        this.tryContent = tryContent;
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

    public String getServiceCycle() {
        return serviceCycle;
    }

    public void setServiceCycle(String serviceCycle) {
        this.serviceCycle = serviceCycle;
    }

    public String getServiceContent() {
        return serviceContent;
    }

    public void setServiceContent(String serviceContent) {
        this.serviceContent = serviceContent;
    }

    public Long[] getTagIds() {
        return tagIds;
    }

    public void setTagIds(Long[] tagIds) {
        this.tagIds = tagIds;
    }
}
