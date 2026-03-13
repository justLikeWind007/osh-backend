package com.backstage.system.domain.order;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 创建订单对象 osh_order_save
 * 
 * @author ruoyi
 * @date 2026-03-07
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OshOrderSave extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long id;

    /**  */
    @JsonProperty("school_id")
    private Long schoolId;

    /**  */
    @JsonProperty("user_id")
    private Long userId;

    /**  */
    @JsonProperty("no")
    private String no;

    /**  */
    @JsonProperty("status")
    private String status;

    /**  */
    @JsonProperty("price")
    private BigDecimal price;

    /**  */
    @JsonProperty("total_price")
    private String totalPrice;

    /**  */
    @JsonProperty("type")
    private String type;

    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @JsonProperty(value = "updated_time")
    private Date updatedTime;

    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "GMT+8")
    @JsonProperty(value = "created_time")
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
