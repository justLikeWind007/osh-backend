package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 首页热门秒杀 VO
 *
 * @author jayTatum
 */
@ApiModel(description = "首页热门秒杀")
public class HotSeckillVO {

    @ApiModelProperty("秒杀活动ID")
    private Long activityId;

    @ApiModelProperty("秒杀活动商品明细ID")
    private Long itemId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品类型：1-课程 2-书籍 3-商品")
    private Integer goodsType;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品封面")
    private String goodsCover;

    @ApiModelProperty("原价")
    private BigDecimal originPrice;

    @ApiModelProperty("秒杀价")
    private BigDecimal seckillPrice;

    @ApiModelProperty("总库存")
    private Integer stock;

    @ApiModelProperty("已售数量")
    private Integer soldCount;

    @ApiModelProperty("每人限购数量")
    private Integer limitPerUser;

    @ApiModelProperty("详情页跳转路径")
    private String detailUrl;

    // ========== getter / setter ==========

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public Integer getGoodsType() { return goodsType; }
    public void setGoodsType(Integer goodsType) { this.goodsType = goodsType; }

    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }

    public String getGoodsCover() { return goodsCover; }
    public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }

    public BigDecimal getSeckillPrice() { return seckillPrice; }
    public void setSeckillPrice(BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }

    public Integer getLimitPerUser() { return limitPerUser; }
    public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }

    public String getDetailUrl() { return detailUrl; }
    public void setDetailUrl(String detailUrl) { this.detailUrl = detailUrl; }
}
