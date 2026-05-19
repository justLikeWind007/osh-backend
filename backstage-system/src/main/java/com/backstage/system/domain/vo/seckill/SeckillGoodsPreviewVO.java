package com.backstage.system.domain.vo.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀商品预览 VO
 * 用于前端选好 goodsType + goodsId 后，拉取商品基本信息自动回填表单
 *
 * @author backstage
 */
@ApiModel(description = "秒杀商品预览信息")
public class SeckillGoodsPreviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品封面图URL")
    private String goodsCover;

    @ApiModelProperty("商品原价")
    private BigDecimal originPrice;

    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }

    public String getGoodsCover() { return goodsCover; }
    public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }
}
