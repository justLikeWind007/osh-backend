package com.backstage.system.request.tool;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具新增/修改请求
 */
@ApiModel(description = "工具新增/修改请求")
public class ToolSaveRequest {

    @ApiModelProperty(value = "工具ID，新增不传，修改必传", example = "10001")
    private Long id;

    @ApiModelProperty(value = "工具名称", required = true, example = "图片转PDF")
    private String toolName;

    @ApiModelProperty(value = "工具描述", example = "支持多张图片合成为PDF文件")
    private String description;

    @ApiModelProperty(value = "工具图标相对路径", example = "common/image/tool/202605/logo.png")
    private String logoUrl;

    @ApiModelProperty(value = "访问类型：1-站内工具，2-iframe第三方工具", example = "1")
    private Integer accessType;

    @ApiModelProperty(value = "站内工具前端路由", example = "/tool/image-to-pdf")
    private String routePath;

    @ApiModelProperty(value = "第三方iframe地址", example = "https://example.com/tool")
    private String iframeUrl;

    @ApiModelProperty(value = "GitHub地址", example = "https://github.com/example/tool")
    private String githubUrl;

    @DecimalMin(value = "0.00", message = "当前价格不能小于0")
    @ApiModelProperty(value = "当前价格", example = "0.00")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "原价不能小于0")
    @ApiModelProperty(value = "原价/市场价", example = "19.90")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "单次消耗积分/余额", example = "0")
    private Integer pointCost;

    @ApiModelProperty(value = "状态：0-待审核，1-上架，2-下架", example = "1")
    private Integer status;

    @ApiModelProperty(value = "备注", example = "首批上线工具")
    private String remark;

    @ApiModelProperty(value = "资源类型：FREE,CASH_ONLY,CASH_POINT,VIP,SMALL_CLASS,INTERNAL", example = "FREE")
    private String resourceType;

    @ApiModelProperty(value = "资源等级", example = "1")
    private Integer level;

    @ApiModelProperty(value = "标签名称列表，不存在的标签会自动创建", example = "[\"PDF工具\",\"图片工具\"]")
    private List<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = StringUtils.trimToNull(toolName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimToNull(description);
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = StringUtils.trimToNull(logoUrl);
    }

    public Integer getAccessType() {
        return accessType;
    }

    public void setAccessType(Integer accessType) {
        this.accessType = accessType;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = StringUtils.trimToNull(routePath);
    }

    public String getIframeUrl() {
        return iframeUrl;
    }

    public void setIframeUrl(String iframeUrl) {
        this.iframeUrl = StringUtils.trimToNull(iframeUrl);
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = StringUtils.trimToNull(githubUrl);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getPointCost() {
        return pointCost;
    }

    public void setPointCost(Integer pointCost) {
        this.pointCost = pointCost;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = StringUtils.trimToNull(remark);
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = StringUtils.trimToNull(resourceType);
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        if (tags == null) {
            this.tags = null;
            return;
        }
        List<String> normalized = new ArrayList<>();
        for (String tag : tags) {
            String value = StringUtils.trimToNull(tag);
            if (value != null && !normalized.contains(value)) {
                normalized.add(value);
            }
        }
        this.tags = normalized;
    }
}
