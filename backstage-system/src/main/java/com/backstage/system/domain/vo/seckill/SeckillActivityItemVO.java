package com.backstage.system.domain.vo.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

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

    @ApiModelProperty("关联活动ID")
    private Long activityId;

    @ApiModelProperty("关联秒杀商品池ID")
    private Long seckillGoodsId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品类型：1-课程 2-书籍 3-实物商品")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
}
