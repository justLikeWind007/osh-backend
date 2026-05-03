package com.backstage.system.domain.vo.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品池响应 VO
 * 只包含前端需要展示的字段，不暴露 deleteFlag、createBy 等内部管理字段
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "秒杀商品池响应")
public class SeckillGoodsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("关联商品ID")
    private Long goodsId;

    @ApiModelProperty("商品类型：1-课程 2-书籍 3-实物商品")
    private Integer goodsType;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品封面图")
    private String goodsCover;

    @ApiModelProperty("商品原价")
    private BigDecimal originPrice;

    @ApiModelProperty("允许的最低秒杀价")
    private BigDecimal minSeckillPrice;

    @ApiModelProperty("状态：0-待审核 1-已上架 2-已下架")
    private Integer status;

    @ApiModelProperty("排序权重，数值越大越靠前")
    private Integer sort;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public BigDecimal getMinSeckillPrice() { return minSeckillPrice; }
    public void setMinSeckillPrice(BigDecimal minSeckillPrice) { this.minSeckillPrice = minSeckillPrice; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
