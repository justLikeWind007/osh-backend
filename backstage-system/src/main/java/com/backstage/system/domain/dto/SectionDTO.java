package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 章节信息 DTO
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "章节信息")
public class SectionDTO {
    
    /** 章节 ID */
    @ApiModelProperty("章节 ID")
    private Long id;
    
    /** 是否可以学习 */
    @ApiModelProperty("是否可以学习")
    private Boolean canLearn;
    
    /** 是否免费 */
    @ApiModelProperty("是否免费")
    private Boolean isFree;
    
    /** 视频 URL */
    @ApiModelProperty("视频 URL")
    private String videoUrl;
    
    /** 文本内容 */
    @ApiModelProperty("文本内容")
    private String textContent;
    
    /** 是否需要购买 */
    @ApiModelProperty("是否需要购买")
    private Boolean needPurchase;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCanLearn() {
        return canLearn;
    }

    public void setCanLearn(Boolean canLearn) {
        this.canLearn = canLearn;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public void setFree(Boolean free) {
        this.isFree = free;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Boolean getNeedPurchase() {
        return needPurchase;
    }

    public void setNeedPurchase(Boolean needPurchase) {
        this.needPurchase = needPurchase;
    }
}
