package com.backstage.system.domain.vo.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 管理端秒杀订单列表 VO
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "管理端秒杀订单响应")
public class SeckillOrderAdminVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单主键ID")
    private Long id;

    @ApiModelProperty("秒杀订单编号")
    private String seckillNo;

    @ApiModelProperty("关联活动ID")
    private Long activityId;

    @ApiModelProperty("购买用户ID")
    private Long userId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品类型：1-课程 2-书籍")
    private Integer goodsType;

    @ApiModelProperty("商品标题快照")
    private String goodsTitle;

    @ApiModelProperty("原价快照")
    private BigDecimal originPrice;

    @ApiModelProperty("秒杀单价快照")
    private BigDecimal seckillPrice;

    @ApiModelProperty("实付总金额（seckillPrice × quantity）")
    private BigDecimal totalAmount;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSeckillNo() { return seckillNo; }
    public void setSeckillNo(String seckillNo) { this.seckillNo = seckillNo; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public Integer getGoodsType() { return goodsType; }
    public void setGoodsType(Integer goodsType) { this.goodsType = goodsType; }

    public String getGoodsTitle() { return goodsTitle; }
    public void setGoodsTitle(String goodsTitle) { this.goodsTitle = goodsTitle; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }

    public BigDecimal getSeckillPrice() { return seckillPrice; }
    public void setSeckillPrice(BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

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

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
