package com.backstage.system.domain.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@ApiModel(description = "待审核资源列表项")
public class ResourceAuditItemVO {

    @ApiModelProperty(value = "资源ID", example = "10001")
    private Long id;

    @ApiModelProperty(value = "资源标题", example = "网页计算器")
    private String title;

    @ApiModelProperty(value = "资源描述", example = "在线计算工具")
    private String description;

    @ApiModelProperty(value = "封面/图标", example = "common/image/tool/logo.png")
    private String cover;

    @ApiModelProperty(value = "资源链接", example = "https://example.com")
    private String url;

    @ApiModelProperty(value = "资源等级", example = "1")
    private Integer level;

    @ApiModelProperty(value = "状态", example = "0")
    private Integer status;

    @ApiModelProperty(value = "创建人", example = "admin")
    private String createBy;

    @ApiModelProperty(value = "创建时间", example = "2026-05-12 10:00:00")
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
