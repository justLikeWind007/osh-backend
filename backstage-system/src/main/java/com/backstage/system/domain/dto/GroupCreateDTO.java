package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发起拼团请求DTO
 * 
 * @author system
 * @date 2026-04-30
 */
@ApiModel("发起拼团请求参数")
public class GroupCreateDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 拼团活动ID */
    @ApiModelProperty(value = "拼团活动ID", required = true)
    @NotNull(message = "拼团活动ID不能为空")
    private Long activityId;
    
    /** 最低拼团人数 */
    @ApiModelProperty(value = "最低拼团人数", required = true)
    @NotNull(message = "最低拼团人数不能为空")
    @Min(value = 2, message = "最低拼团人数不能少于2人")
    private Integer minNum;
    
    /** 最多拼团人数 */
    @ApiModelProperty(value = "最多拼团人数", required = true)
    @NotNull(message = "最多拼团人数不能为空")
    @Min(value = 2, message = "最多拼团人数不能少于2人")
    private Integer maxNum;
    
    /** 支付费用 */
    @ApiModelProperty(value = "支付费用", required = true)
    @NotNull(message = "支付费用不能为空")
    @DecimalMin(value = "0.01", message = "支付费用必须大于0")
    private BigDecimal price;
    
    /** 服务器使用时间（月） */
    @ApiModelProperty(value = "服务器使用时间（月）", required = true)
    @NotNull(message = "服务器使用时间不能为空")
    @Min(value = 1, message = "使用时间不能少于1个月")
    @Max(value = 120, message = "使用时间不能超过120个月")
    private Integer duration;
    
    public Long getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
    public Integer getMinNum() {
        return minNum;
    }
    
    public void setMinNum(Integer minNum) {
        this.minNum = minNum;
    }
    
    public Integer getMaxNum() {
        return maxNum;
    }
    
    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
