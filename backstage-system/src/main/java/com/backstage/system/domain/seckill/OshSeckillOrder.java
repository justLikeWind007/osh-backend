package com.backstage.system.domain.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀订单对象 osh_seckill_order
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "秒杀订单")
public class OshSeckillOrder {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("秒杀订单编号")
    private String seckillNo;

    @ApiModelProperty("关联秒杀活动ID")
    private Long activityId;

    @ApiModelProperty("关联秒杀活动商品明细ID")
    private Long itemId;

    @ApiModelProperty("购买用户ID")
    private Long userId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品类型：1-课程 2-书籍")
    private Integer goodsType;

    @ApiModelProperty("商品标题快照")
    private String goodsTitle;

    @ApiModelProperty("商品封面快照")
    private String goodsCover;

    @ApiModelProperty("原价快照")
    private BigDecimal originPrice;

    @ApiModelProperty("秒杀价格快照")
    private BigDecimal seckillPrice;

    @ApiModelProperty("购买数量")
    private Integer quantity;

    @ApiModelProperty("订单状态：0-待支付 1-已支付 2-已取消 3-已超时 4-已退款")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("实际支付时间")
    private Date payTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("支付截止时间")
    private Date payExpireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("取消时间")
    private Date cancelTime;

    @ApiModelProperty("取消原因")
    private String cancelReason;

    @ApiModelProperty("关联主订单编号")
    private String oshOrderNo;

    @ApiModelProperty("删除标记：0-正常 1-已删除")
    private Integer deleteFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSeckillNo() { return seckillNo; }
    public void setSeckillNo(String seckillNo) { this.seckillNo = seckillNo; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }

    public Date getPayExpireTime() { return payExpireTime; }
    public void setPayExpireTime(Date payExpireTime) { this.payExpireTime = payExpireTime; }

    public Date getCancelTime() { return cancelTime; }
    public void setCancelTime(Date cancelTime) { this.cancelTime = cancelTime; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public String getOshOrderNo() { return oshOrderNo; }
    public void setOshOrderNo(String oshOrderNo) { this.oshOrderNo = oshOrderNo; }

    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
