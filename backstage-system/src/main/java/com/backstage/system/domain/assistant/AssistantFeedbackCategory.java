package com.backstage.system.domain.assistant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * AI 助手反馈分类实体
 *
 * @author backstage
 */
@TableName("assistant_feedback_category")
public class AssistantFeedbackCategory {

    /**
     * 分类 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类代码（唯一标识）
     */
    private String code;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 驱动力（用户提交该类型反馈的动机）
     */
    private String driveForce;

    /**
     * 期望结果（用户期望得到的反馈）
     */
    private String expectedResult;

    /**
     * 语气倾向（该类型反馈的典型语气）
     */
    private String toneTendency;

    /**
     * 分类图标（Emoji 或图标类名）
     */
    private String icon;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 是否仅管理员可用（0-否 1-是）
     */
    private Integer isAdminOnly;

    /**
     * 是否允许评论（0-否 1-是）
     */
    private Integer allowComment;

    /**
     * 是否启用（0-否 1-是）
     */
    private Integer isEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDriveForce() {
        return driveForce;
    }

    public void setDriveForce(String driveForce) {
        this.driveForce = driveForce;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getToneTendency() {
        return toneTendency;
    }

    public void setToneTendency(String toneTendency) {
        this.toneTendency = toneTendency;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getIsAdminOnly() {
        return isAdminOnly;
    }

    public void setIsAdminOnly(Integer isAdminOnly) {
        this.isAdminOnly = isAdminOnly;
    }

    public Integer getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Integer allowComment) {
        this.allowComment = allowComment;
    }

    public Integer getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
