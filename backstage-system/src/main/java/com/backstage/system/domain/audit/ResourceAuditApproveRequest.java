package com.backstage.system.domain.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "资源审核通过请求")
public class ResourceAuditApproveRequest {

    @ApiModelProperty(value = "资源类型：course / qa_question / qa_answer / book / tool / website", required = true, example = "tool")
    @NotBlank(message = "资源类型不能为空")
    private String resourceType;

    @ApiModelProperty(value = "资源ID", required = true, example = "10001")
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;

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
}
