package com.backstage.system.domain.dto.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 添加商品到秒杀商品池 DTO
 * goodsName / goodsCover / originPrice 由后端根据 goodsType 自动从对应表查询填充，前端无需传入
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "添加秒杀商品请求")
public class SeckillGoodsAddDTO {

    @NotNull(message = "商品ID不能为空")
    @ApiModelProperty(value = "关联商品ID", required = true)
    private Long goodsId;

    @NotNull(message = "商品类型不能为空")
    @ApiModelProperty(value = "商品类型：1-课程 2-书籍", required = true)
    private Integer goodsType;

    @NotNull(message = "最低秒杀价不能为空")
    @DecimalMin(value = "0.01", message = "最低秒杀价必须大于0")
    @ApiModelProperty(value = "允许的最低秒杀价", required = true)
    private BigDecimal minSeckillPrice;

    @ApiModelProperty("排序权重，数值越大越靠前，默认0")
    private Integer sort;

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public Integer getGoodsType() { return goodsType; }
    public void setGoodsType(Integer goodsType) { this.goodsType = goodsType; }

    public BigDecimal getMinSeckillPrice() { return minSeckillPrice; }
    public void setMinSeckillPrice(BigDecimal minSeckillPrice) { this.minSeckillPrice = minSeckillPrice; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
}
