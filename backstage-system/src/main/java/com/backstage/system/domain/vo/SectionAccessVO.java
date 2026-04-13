package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 章节访问权限 VO
 * 用于返回用户对某章节的访问权限信息
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
@ApiModel(description = "章节访问权限")
public class SectionAccessVO {
    
    /** 章节 ID */
    @ApiModelProperty("章节ID")
    private Long sectionId;
    
    /** 是否有访问权限 */
    @ApiModelProperty("是否有访问权限")
    private Boolean hasAccess;
    
    /** 是否免费章节 */
    @ApiModelProperty("是否免费章节")
    private Boolean isFree;
    
    /** 是否需要购买 */
    @ApiModelProperty("是否需要购买")
    private Boolean needPurchase;
    
    /** 用户是否已购买课程 */
    @ApiModelProperty("用户是否已购买课程")
    private Boolean isPurchased;
    
    /** 无权限原因 */
    @ApiModelProperty("无权限原因：未登录、未购买、课程已下架等")
    private String reason;
    
    /** 课程价格（当需要购买时显示） */
    @ApiModelProperty("课程价格")
    private String price;

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Boolean getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(Boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Boolean getNeedPurchase() {
        return needPurchase;
    }

    public void setNeedPurchase(Boolean needPurchase) {
        this.needPurchase = needPurchase;
    }

    public Boolean getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(Boolean isPurchased) {
        this.isPurchased = isPurchased;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
