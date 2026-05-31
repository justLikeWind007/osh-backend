package com.backstage.system.domain.dto.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 修改秒杀商品信息 DTO
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "修改秒杀商品请求")
public class SeckillGoodsUpdateDTO {

    @NotNull(message = "商品池ID不能为空")
    @ApiModelProperty(value = "商品池主键ID", required = true)
    private Long id;

    @ApiModelProperty("商品名称快照")
    private String goodsName;

    @ApiModelProperty("商品封面快照")
    private String goodsCover;

    @DecimalMin(value = "0.01", message = "商品原价必须大于0")
    @ApiModelProperty("商品原价快照")
    private BigDecimal originPrice;

    @DecimalMin(value = "0.01", message = "最低秒杀价必须大于0")
    @ApiModelProperty("允许的最低秒杀价")
    private BigDecimal minSeckillPrice;

    @ApiModelProperty("排序权重，数值越大越靠前")
    private Integer sort;

    @ApiModelProperty("标签名称列表（全量替换，传空列表则清空标签）")
    private List<String> tagNames;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }

    public String getGoodsCover() { return goodsCover; }
    public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }

    public BigDecimal getMinSeckillPrice() { return minSeckillPrice; }
    public void setMinSeckillPrice(BigDecimal minSeckillPrice) { this.minSeckillPrice = minSeckillPrice; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }
}
