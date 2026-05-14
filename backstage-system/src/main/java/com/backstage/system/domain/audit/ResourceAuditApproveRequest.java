package com.backstage.system.domain.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "资源审核请求")
public class ResourceAuditApproveRequest {

    @ApiModelProperty(value = "资源类型：course / qa_question / qa_answer / book / tool / website / open_project", required = true, example = "open_project")
    @NotBlank(message = "资源类型不能为空")
    private String resourceType;

    @ApiModelProperty(value = "资源ID", required = true, example = "10001")
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;

    @ApiModelProperty(value = "审核动作：1-通过并将资源状态改为4，2-拒绝并将资源状态改为6", example = "1")
    private Integer status;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
