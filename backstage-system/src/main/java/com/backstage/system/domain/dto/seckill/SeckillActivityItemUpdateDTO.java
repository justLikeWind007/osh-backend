package com.backstage.system.domain.dto.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 秒杀活动商品明细 修改DTO
 * 修改活动时，items 列表中每一项使用此 DTO
 * id 不为空表示修改已有明细，id 为空表示新增明细
 *
 * @author backstage
 * @date 2026-05-04
 */
@ApiModel(description = "秒杀活动商品明细修改请求")
public class SeckillActivityItemUpdateDTO {

    @ApiModelProperty("明细ID（不传表示新增该明细，传入则修改对应明细）")
    private Long id;

    @NotNull(message = "秒杀商品池ID不能为空")
    @ApiModelProperty(value = "秒杀商品池ID", required = true)
    private Long seckillGoodsId;

    @ApiModelProperty("商品标题")
    private String title;

    @ApiModelProperty("封面图URL")
    private String cover;

    @DecimalMin(value = "0.01", message = "秒杀价格必须大于0")
    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    @Min(value = 1, message = "库存至少为1")
    @ApiModelProperty("总库存")
    private Integer totalStock;

    @Min(value = 1, message = "每人限购至少为1")
    @ApiModelProperty("每人限购数量")
    private Integer limitPerUser;

    @ApiModelProperty("展示排序")
    private Integer sort;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSeckillGoodsId() { return seckillGoodsId; }
    public void setSeckillGoodsId(Long seckillGoodsId) { this.seckillGoodsId = seckillGoodsId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public BigDecimal getSeckillPrice() { return seckillPrice; }
    public void setSeckillPrice(BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }

    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }

    public Integer getLimitPerUser() { return limitPerUser; }
    public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
}
