package com.backstage.system.domain.dto.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 秒杀活动商品明细 新增DTO
 * 创建活动时，items 列表中每一项使用此 DTO
 *
 * @author backstage
 * @date 2026-05-04
 */
@ApiModel(description = "秒杀活动商品明细请求")
public class SeckillActivityItemAddDTO {

    @NotNull(message = "秒杀商品池ID不能为空")
    @ApiModelProperty(value = "秒杀商品池ID", required = true)
    private Long seckillGoodsId;

    @ApiModelProperty("商品标题（不传则使用商品池名称）")
    private String title;

    @ApiModelProperty("封面图URL（不传则使用商品池封面）")
    private String cover;

    @NotNull(message = "秒杀价格不能为空")
    @DecimalMin(value = "0.01", message = "秒杀价格必须大于0")
    @ApiModelProperty(value = "该商品在本次活动的秒杀价格", required = true)
    private BigDecimal seckillPrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 1, message = "库存至少为1")
    @ApiModelProperty(value = "该商品在本次活动的总库存", required = true)
    private Integer totalStock;

    @NotNull(message = "每人限购数量不能为空")
    @Min(value = 1, message = "每人限购至少为1")
    @ApiModelProperty(value = "每人限购数量", required = true)
    private Integer limitPerUser;

    @ApiModelProperty("展示排序，数值越小越靠前，默认0")
    private Integer sort;

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
