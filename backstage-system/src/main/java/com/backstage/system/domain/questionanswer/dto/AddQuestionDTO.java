package com.backstage.system.domain.questionanswer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/28
 * Time: 21:12
 */
@ApiModel(description = "新增问题实体类")
public class AddQuestionDTO {
    @ApiModelProperty(
            value = "资源类型",
            required = true
    )
    private String resourceType;
    @ApiModelProperty(
            value = "资源编号"
    )
    private Long resourceNo;
    @ApiModelProperty(
            value = "问题描述",
            required = true
    )
    private String content;
    @ApiModelProperty(
            value = "是否为付费资源",
            required = true
    )
    private Byte isPaidOnly;
    @ApiModelProperty(
            value = "标签id集合"
    )
    private List<Long> tags;
    @ApiModelProperty(
            value = "状态"
    )
    private Byte status;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(Long resourceNo) {
        this.resourceNo = resourceNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Byte getIsPaidOnly() {
        return isPaidOnly;
    }

    public void setIsPaidOnly(Byte isPaidOnly) {
        this.isPaidOnly = isPaidOnly;
    }

    public List<Long> getTags() {
        return tags;
    }

    public void setTags(List<Long> tags) {
        this.tags = tags;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AddQuestionDTO{" +
                "resourceType='" + resourceType + '\'' +
                ", resourceId=" + resourceNo +
                ", content='" + content + '\'' +
                ", isPaidOnly=" + isPaidOnly +
                ", tags=" + tags +
                ", status=" + status +
                '}';
    }
}