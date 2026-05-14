package com.backstage.system.domain.assistant;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 反馈标签实体
 *
 * @author backstage
 */
@TableName("assistant_feedback_tag")
public class AssistantFeedbackTag extends OSHBaseEntity {

    /**
     * 标签 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签编码
     */
    private String code;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 排序值
     */
    private Integer sortOrder;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 是否启用（0-否 1-是）
     */
    private Integer isEnabled;

    /**
     * 标签备注
     */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
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
