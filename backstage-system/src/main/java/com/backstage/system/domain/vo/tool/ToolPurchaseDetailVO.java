package com.backstage.system.domain.vo.tool;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "工具购买详情")
public class ToolPurchaseDetailVO {

    @ApiModelProperty(value = "工具ID", example = "1001")
    private Long toolId;

    @ApiModelProperty(value = "工具名称", example = "AI海报生成器")
    private String toolName;

    @ApiModelProperty(value = "工具描述", example = "用于生成封面与营销海报")
    private String description;

    @ApiModelProperty(value = "当前用户剩余次数", example = "12")
    private Integer remainingCount;

    @ApiModelProperty(value = "套餐列表")
    private List<ToolPurchasePackageVO> packages;

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }

    public List<ToolPurchasePackageVO> getPackages() {
        return packages;
    }

    public void setPackages(List<ToolPurchasePackageVO> packages) {
        this.packages = packages;
    }
}
