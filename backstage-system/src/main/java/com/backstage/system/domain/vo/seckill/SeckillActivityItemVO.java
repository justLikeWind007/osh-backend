package com.backstage.system.domain.vo.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 秒杀活动商品明细 VO
 *
 * @author backstage
 * @date 2026-05-04
 */
@ApiModel(description = "秒杀活动商品明细响应")
public class SeckillActivityItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("明细ID")
    private Long id;

    @ApiModelProperty("资源编号")
    private String no;

    @ApiModelProperty("关联活动ID")
    private Long activityId;

    @ApiModelProperty("关联秒杀商品池ID")
    private Long seckillGoodsId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品类型：1-课程 2-书籍 3-商品")
    private Integer goodsType;

    @ApiModelProperty("商品标题")
    private String title;

    @ApiModelProperty("商品封面图")
    private String cover;

    @ApiModelProperty("商品原价（划线价）")
    private BigDecimal originPrice;

    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty("活动总库存")
    private Integer totalStock;

    @ApiModelProperty("剩余可用库存")
    private Integer availableStock;

    @ApiModelProperty("已售数量")
    private Integer soldCount;

    @ApiModelProperty("每人限购数量")
    private Integer limitPerUser;

    @ApiModelProperty("展示排序")
    private Integer sort;

    @ApiModelProperty("活动标题")
    private String activityTitle;

    @ApiModelProperty("活动状态：1-未开始 2-进行中 3-已结束 4-已下架")
    private Integer activityStatus;

    @ApiModelProperty("支付超时时间（分钟）")
    private Integer payTimeoutMin;

    @ApiModelProperty("活动开始时间")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.util.Date startTime;

    @ApiModelProperty("活动结束时间")
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.util.Date endTime;

    @ApiModelProperty("标签名称列表")
    private List<String> tagNames;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNo() { return no; }
    public void setNo(String no) { this.no = no; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Long getSeckillGoodsId() { return seckillGoodsId; }
    public void setSeckillGoodsId(Long seckillGoodsId) { this.seckillGoodsId = seckillGoodsId; }

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public Integer getGoodsType() { return goodsType; }
    public void setGoodsType(Integer goodsType) { this.goodsType = goodsType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }

    public BigDecimal getSeckillPrice() { return seckillPrice; }
    public void setSeckillPrice(BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }

    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }

    public Integer getLimitPerUser() { return limitPerUser; }
    public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getActivityTitle() { return activityTitle; }
    public void setActivityTitle(String activityTitle) { this.activityTitle = activityTitle; }

    public Integer getActivityStatus() { return activityStatus; }
    public void setActivityStatus(Integer activityStatus) { this.activityStatus = activityStatus; }

    public Integer getPayTimeoutMin() { return payTimeoutMin; }
    public void setPayTimeoutMin(Integer payTimeoutMin) { this.payTimeoutMin = payTimeoutMin; }

    public java.util.Date getStartTime() { return startTime; }
    public void setStartTime(java.util.Date startTime) { this.startTime = startTime; }

    public java.util.Date getEndTime() { return endTime; }
    public void setEndTime(java.util.Date endTime) { this.endTime = endTime; }

    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }
}
