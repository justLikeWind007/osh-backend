package com.backstage.system.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 创建秒杀订单对象 osh_flashsale_list
 * 
 * @author ruoyi
 * @date 2026-03-10
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OshFlashsaleList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @JsonProperty("school_id")
    private String schoolId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    @JsonProperty("user_id")
    private String userId;

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
    @JsonProperty("flashsale_id")
    private String flashsaleId;

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

    public void setSchoolId(String schoolId)
    {
        this.schoolId = schoolId;
    }

    public String getSchoolId()
    {
        return schoolId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
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

        public void setFlashsaleId(String flashsaleId)
    {
        this.flashsaleId = flashsaleId;
    }

    public String getFlashsaleId()
    {
        return flashsaleId;
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
            .append("flashsaleId", getFlashsaleId())
            .append("updatedTime", getUpdatedTime())
            .append("createdTime", getCreatedTime())
            .toString();
    }
}
