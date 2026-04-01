package com.backstage.system.domain.order;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 卡券对象 osh_card
 * 
 * @author ruoyi
 * @date 2026-03-05
 */
@ApiModel("优惠券")
public class Coupon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @ApiModelProperty("优惠券的ID")
    private Long id;

    /** 标题 */
    @Excel(name = "标题")
    @ApiModelProperty("优惠券的名称")
    private String title;

    /** 类型（course 课程/other 其他） */
    @ApiModelProperty("优惠券类型")
    @Excel(name = "类型")
    private String type;

    /** 价格 */
    @Excel(name = "价格")
    @ApiModelProperty("优惠券价格")
    private BigDecimal price;

    /** 商品 ID */
    @Excel(name = "商品 ID")
    @ApiModelProperty("优惠券对应商品的ID")
    private Long goodsId;

    /** 开始时间 */
    @ApiModelProperty("优惠券发放时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @ApiModelProperty("优惠券失效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 是否使用（0 未使用 1 已使用） */
    @ApiModelProperty("优惠券是否使用")
    @Excel(name = "是否使用")
    private Integer used;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setGoodsId(Long goodsId) 
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId() 
    {
        return goodsId;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setUsed(Integer used) 
    {
        this.used = used;
    }

    public Integer getUsed() 
    {
        return used;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("type", getType())
            .append("price", getPrice())
            .append("goodsId", getGoodsId())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("used", getUsed())
            .toString();
    }
}
