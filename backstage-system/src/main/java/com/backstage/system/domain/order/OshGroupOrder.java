package com.backstage.system.domain.order;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 订单对象 osh_group_order
 * 
 * @author ruoyi
 * @date 2026-03-11
 */
public class OshGroupOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID（主键） */
    private Long id;

    /** 所属网校ID */
    @Excel(name = "所属网校ID")
    private Long schoolId;

    /** 下单用户ID */
    @Excel(name = "下单用户ID")
    private Long userId;

    /** 订单编号（唯一） */
    @Excel(name = "订单编号", readConverterExp = "唯=一")
    private String no;

    /** 订单状态：pendding待支付/paid已支付/cancel已取消 */
    @Excel(name = "订单状态：pendding待支付/paid已支付/cancel已取消")
    private String status;

    /** 实际支付价格 */
    @Excel(name = "实际支付价格")
    private BigDecimal price;

    /** 商品原价 */
    @Excel(name = "商品原价")
    private BigDecimal totalPrice;

    /** 订单类型：group拼团 */
    @Excel(name = "订单类型：group拼团")
    private String type;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date updatedTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date createdTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setSchoolId(Long schoolId) 
    {
        this.schoolId = schoolId;
    }

    public Long getSchoolId() 
    {
        return schoolId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setNo(String no) 
    {
        this.no = no;
    }

    public String getNo() 
    {
        return no;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setTotalPrice(BigDecimal totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() 
    {
        return totalPrice;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setUpdatedTime(Date updatedTime) 
    {
        this.updatedTime = updatedTime;
    }

    public Date getUpdatedTime() 
    {
        return updatedTime;
    }

    public void setCreatedTime(Date createdTime) 
    {
        this.createdTime = createdTime;
    }

    public Date getCreatedTime() 
    {
        return createdTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("schoolId", getSchoolId())
            .append("userId", getUserId())
            .append("no", getNo())
            .append("status", getStatus())
            .append("price", getPrice())
            .append("totalPrice", getTotalPrice())
            .append("type", getType())
            .append("updatedTime", getUpdatedTime())
            .append("createdTime", getCreatedTime())
            .toString();
    }
}
