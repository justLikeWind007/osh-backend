package com.backstage.system.domain.website;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 网站用户评价记录表
 * @TableName osh_website_user_rating
 */
@TableName(value ="osh_website_user_rating")
public class OshWebsiteUserRating {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 网站ID
     */
    private Long websiteId;

    /**
     * 评价类型: 1-好评, 2-中评, 3-差评
     */
    private Integer ratingType;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 删除标记 0-未删除 1-已删除
     */
    private Integer deleteFlag;

    /**
     * 好评数量
     */
    private Integer goodCount;

    /**
     * 中评数量
     */
    private Integer midCount;

    /**
     * 差评数量
     */
    private Integer badCount;

    /**
     * 收藏数量
     */
    private Integer collectionCount;

    /**
     * 评分
     */
    private BigDecimal ratingScore;

    /**
     * 点击数量
     */
    private Integer clickCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 网站ID
     */
    public Long getWebsiteId() {
        return websiteId;
    }

    /**
     * 网站ID
     */
    public void setWebsiteId(Long websiteId) {
        this.websiteId = websiteId;
    }

    /**
     * 评价类型: 1-好评, 2-中评, 3-差评
     */
    public Integer getRatingType() {
        return ratingType;
    }

    /**
     * 评价类型: 1-好评, 2-中评, 3-差评
     */
    public void setRatingType(Integer ratingType) {
        this.ratingType = ratingType;
    }

    /**
     * 创建人ID
     */
    public Long getCreateBy() {
        return createBy;
    }

    /**
     * 创建人ID
     */
    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    /**
     * 更新人ID
     */
    public Long getUpdateBy() {
        return updateBy;
    }

    /**
     * 更新人ID
     */
    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * 删除标记 0-未删除 1-已删除
     */
    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    /**
     * 删除标记 0-未删除 1-已删除
     */
    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    /**
     * 好评数量
     */
    public Integer getGoodCount() {
        return goodCount;
    }

    /**
     * 好评数量
     */
    public void setGoodCount(Integer goodCount) {
        this.goodCount = goodCount;
    }

    /**
     * 中评数量
     */
    public Integer getMidCount() {
        return midCount;
    }

    /**
     * 中评数量
     */
    public void setMidCount(Integer midCount) {
        this.midCount = midCount;
    }

    /**
     * 差评数量
     */
    public Integer getBadCount() {
        return badCount;
    }

    /**
     * 差评数量
     */
    public void setBadCount(Integer badCount) {
        this.badCount = badCount;
    }

    /**
     * 收藏数量
     */
    public Integer getCollectionCount() {
        return collectionCount;
    }

    /**
     * 收藏数量
     */
    public void setCollectionCount(Integer collectionCount) {
        this.collectionCount = collectionCount;
    }

    /**
     * 评分
     */
    public BigDecimal getRatingScore() {
        return ratingScore;
    }

    /**
     * 评分
     */
    public void setRatingScore(BigDecimal ratingScore) {
        this.ratingScore = ratingScore;
    }

    /**
     * 点击数量
     */
    public Integer getClickCount() {
        return clickCount;
    }

    /**
     * 点击数量
     */
    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "WebsiteRating{" +
                "id=" + id +
                ", userId=" + userId +
                ", websiteId=" + websiteId +
                ", ratingType=" + ratingType +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                ", deleteFlag=" + deleteFlag +
                ", goodCount=" + goodCount +
                ", midCount=" + midCount +
                ", badCount=" + badCount +
                ", collectionCount=" + collectionCount +
                ", ratingScore=" + ratingScore +
                ", clickCount=" + clickCount +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}