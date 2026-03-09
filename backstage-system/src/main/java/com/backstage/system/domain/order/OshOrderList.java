package com.backstage.system.domain.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 我的订单列对象 osh_order_list
 * 
 * @author ruoyi
 * @date 2026-03-09
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "no", "price", "status", "totalPrice", "createdTime", "goods"})
public class OshOrderList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String no;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String price;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String status;

    /** $column.columnComment */
    @JsonProperty("total_price")
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String totalPrice;

    /** $column.columnComment */
    @JsonProperty("created_time")
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String createdTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String goods;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setNo(String no) 
    {
        this.no = no;
    }

    public String getNo() 
    {
        return no;
    }

    public void setPrice(String price) 
    {
        this.price = price;
    }

    public String getPrice() 
    {
        return price;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setTotalPrice(String totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public String getTotalPrice() 
    {
        return totalPrice;
    }

    public void setCreatedTime(String createdTime) 
    {
        this.createdTime = createdTime;
    }

    public String getCreatedTime() 
    {
        return createdTime;
    }

    public void setGoods(String goods) 
    {
        this.goods = goods;
    }

    public String getGoods() 
    {
        return goods;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("no", getNo())
            .append("price", getPrice())
            .append("status", getStatus())
            .append("totalPrice", getTotalPrice())
            .append("createdTime", getCreatedTime())
            .append("goods", getGoods())
            .toString();
    }
}
