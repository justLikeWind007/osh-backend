package com.backstage.system.domain.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

import javax.validation.constraints.NotNull;

/**
 * 立即学习对象 osh_learn
 * 
 * @author ruoyi
 * @date 2026-03-10
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OshLearn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long schoolId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long userId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String no;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String status;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String price;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @JsonProperty("total_price")
    private String totalPrice;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String type;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @JsonProperty("pay_method")
    private String payMethod;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @JsonProperty("pay_time")
    private String payTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @JsonProperty("updated_time")
    private String updatedTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @JsonProperty("created_time")
    private String createdTime;

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

    public void setPrice(String price) 
    {
        this.price = price;
    }

    public String getPrice() 
    {
        return price;
    }

    public void setTotalPrice(String totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public String getTotalPrice() 
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

    public void setPayMethod(String payMethod) 
    {
        this.payMethod = payMethod;
    }

    public String getPayMethod() 
    {
        return payMethod;
    }

    public void setPayTime(String payTime) 
    {
        this.payTime = payTime;
    }

    public String getPayTime() 
    {
        return payTime;
    }

    public void setUpdatedTime(String updatedTime) 
    {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedTime() 
    {
        return updatedTime;
    }

    public void setCreatedTime(String createdTime) 
    {
        this.createdTime = createdTime;
    }

    public String getCreatedTime() 
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
            .append("payMethod", getPayMethod())
            .append("payTime", getPayTime())
            .append("updatedTime", getUpdatedTime())
            .append("createdTime", getCreatedTime())
            .toString();
    }
}
