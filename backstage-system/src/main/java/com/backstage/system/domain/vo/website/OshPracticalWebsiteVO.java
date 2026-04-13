package com.backstage.system.domain.vo.website;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 实用网站视图对象（带标签信息）
 */
public class OshPracticalWebsiteVO {

    /** 主键 ID */
    private Long id;

    /** 网站名称 */
    private String name;

    /** 网站链接 */
    private String url;

    /** 网站描述 */
    private String description;

    /** 网站 Logo 地址 */
    private String logoUrl;

    /** 标签集合（从关联表查询，逗号分隔） */
    private String tags;

    /** 点击次数 */
    private Integer clickCount;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /** 好评数量 */
    private Integer goodCount;

    /** 中评数量 */
    private Integer midCount;

    /** 差评数量 */
    private Integer badCount;

    /** 收藏数量 */
    private Integer collectionCount;

    /** 评分 */
    private BigDecimal ratingScore;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
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

    public Integer getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(Integer collectionCount) {
        this.collectionCount = collectionCount;
    }

    public BigDecimal getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(BigDecimal ratingScore) {
        this.ratingScore = ratingScore;
    }
    @Override
    public String toString() {
        return "OshPracticalWebsiteVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", tags='" + tags + '\'' +
                ", clickCount=" + clickCount +
                ", auditTime=" + auditTime +
                ", goodCount=" + goodCount +
                ", midCount=" + midCount +
                ", badCount=" + badCount +
                ", collectionCount=" + collectionCount +
                ", ratingScore=" + ratingScore +
                '}';
    }
}
