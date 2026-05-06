package com.backstage.system.domain.seckill;

import com.backstage.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀活动商品明细 osh_seckill_activity_item
 * 一个活动可包含多个商品明细，每个明细有独立的库存、秒杀价、限购数
 *
 * @author backstage
 * @date 2026-05-04
 */
@ApiModel(description = "秒杀活动商品明细")
public class OshSeckillActivityItem {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @Excel(name = "活动ID")
    @ApiModelProperty("关联秒杀活动ID")
    private Long activityId;

    @Excel(name = "秒杀商品池ID")
    @ApiModelProperty("关联秒杀商品池ID")
    private Long seckillGoodsId;

    @Excel(name = "商品ID")
    @ApiModelProperty("关联商品ID（冗余快照）")
    private Long goodsId;

    @Excel(name = "商品类型")
    @ApiModelProperty("商品类型：1-课程 2-书籍 3-商品")
    private Integer goodsType;

    @Excel(name = "商品标题")
    @ApiModelProperty("商品标题快照")
    private String title;

    @Excel(name = "封面图")
    @ApiModelProperty("商品封面图快照")
    private String cover;

    @Excel(name = "原价")
    @ApiModelProperty("商品原价快照")
    private BigDecimal originPrice;

    @Excel(name = "秒杀价格")
    @ApiModelProperty("该商品在本次活动的秒杀价格")
    private BigDecimal seckillPrice;

    @Excel(name = "总库存")
    @ApiModelProperty("该商品在本次活动的总库存")
    private Integer totalStock;

    @Excel(name = "剩余库存")
    @ApiModelProperty("剩余可用库存")
    private Integer availableStock;

    @Excel(name = "已售数量")
    @ApiModelProperty("已售数量")
    private Integer soldCount;

    @Excel(name = "每人限购")
    @ApiModelProperty("每人限购数量")
    private Integer limitPerUser;

    @Excel(name = "排序")
    @ApiModelProperty("在活动内的展示排序，数值越小越靠前")
    private Integer sort;

    @ApiModelProperty("创建人ID")
    private Long createBy;

    @ApiModelProperty("更新人ID")
    private Long updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标记：0-正常 1-已删除")
    private Integer deleteFlag;

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

    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }

    public Long getUpdateBy() { return updateBy; }
    public void setUpdateBy(Long updateBy) { this.updateBy = updateBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }
}
