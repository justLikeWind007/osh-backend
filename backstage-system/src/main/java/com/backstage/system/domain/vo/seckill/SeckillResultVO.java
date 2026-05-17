package com.backstage.system.domain.vo.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀结果 VO（接口10返回 + 接口11轮询使用）
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "秒杀结果响应")
public class SeckillResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("秒杀订单编号")
    private String seckillNo;

    @ApiModelProperty("订单状态：-1-处理中(Kafka消费中) 0-待支付 1-已支付 2-已取消 3-已超时 4-已退款")
    private Integer status;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品类型：1-课程 2-书籍")
    private Integer goodsType;

    @ApiModelProperty("商品标题")
    private String goodsTitle;

    @ApiModelProperty("商品封面")
    private String goodsCover;

    @ApiModelProperty("原价")
    private BigDecimal originPrice;

    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty("购买数量")
    private Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("支付截止时间（前端用于倒计时）")
    private Date payExpireTime;

    public String getSeckillNo() { return seckillNo; }
    public void setSeckillNo(String seckillNo) { this.seckillNo = seckillNo; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public Integer getGoodsType() { return goodsType; }
    public void setGoodsType(Integer goodsType) { this.goodsType = goodsType; }

    public String getGoodsTitle() { return goodsTitle; }
    public void setGoodsTitle(String goodsTitle) { this.goodsTitle = goodsTitle; }

    public String getGoodsCover() { return goodsCover; }
    public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }

    public BigDecimal getSeckillPrice() { return seckillPrice; }
    public void setSeckillPrice(BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Date getPayExpireTime() { return payExpireTime; }
    public void setPayExpireTime(Date payExpireTime) { this.payExpireTime = payExpireTime; }
}
