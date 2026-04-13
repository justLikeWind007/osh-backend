package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/9
 * Time: 19:40
 */
@ApiModel(description = "标记违规实体类")
public class UserRecordDTO {
    @ApiModelProperty(
            value = "违规用户id",
            required = true
    )
    private Long userId;
    @ApiModelProperty(
            value = "违规类型：1=乱答，2=广告，3=恶意灌水，4=其他",
            required = true
    )
    private Integer violationType;
    @ApiModelProperty(
            value = "违规原因",
            required = true
    )
    private String reason;
    @ApiModelProperty(
            value = "操作者id"
    )
    private Long operatorId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getViolationType() {
        return violationType;
    }

    public void setViolationType(Integer violationType) {
        this.violationType = violationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String toString() {
        return "UserRecordDTO{" +
                "userId=" + userId +
                ", violationType=" + violationType +
                ", reason='" + reason + '\'' +
                ", operatorId=" + operatorId +
                '}';
    }
}
