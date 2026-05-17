package com.backstage.system.domain.assistant.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 反馈标签创建请求 DTO
 *
 * @author backstage
 */
public class AssistantFeedbackTagCreateDTO {

    /**
     * 标签编码，不传时根据标签名称自动生成
     */
    @Size(max = 64, message = "标签编码不能超过64个字符")
    private String code;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 64, message = "标签名称不能超过64个字符")
    private String name;

    /**
     * 排序值
     */
    private Integer sortOrder;

    /**
     * 是否启用（0-否 1-是）
     */
    @Min(value = 0, message = "启用状态不正确")
    @Max(value = 1, message = "启用状态不正确")
    private Integer isEnabled;

    /**
     * 标签备注
     */
    @Size(max = 255, message = "标签备注不能超过255个字符")
    private String remark;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
