package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 手动添加用户到拼团请求DTO
 * 
 * @author system
 * @date 2026-05-05
 */
@ApiModel("手动添加用户到拼团请求参数")
public class AddUserToGroupDTO {
    
    /** 拼团活动ID（用户发起的拼团记录ID） */
    @ApiModelProperty(value = "拼团活动ID", required = true, example = "1234567890")
    @NotNull(message = "拼团活动ID不能为空")
    private Long activityId;
    
    /** 用户ID */
    @ApiModelProperty(value = "用户ID", required = true, example = "1720100000001")
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /** 备注说明 */
    @ApiModelProperty(value = "备注说明", example = "线下已付款，由管理员手动录入")
    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
    
    // Getter and Setter
    
    public Long getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
