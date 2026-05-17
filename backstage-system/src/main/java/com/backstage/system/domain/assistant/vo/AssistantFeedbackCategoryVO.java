package com.backstage.system.domain.assistant.vo;

/**
 * AI 助手反馈分类响应 VO
 *
 * @author backstage
 */
public class AssistantFeedbackCategoryVO {

    /**
     * 分类 ID
     */
    private Long id;

    /**
     * 分类代码
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
     * 驱动力
     */
    private String driveForce;

    /**
     * 期望结果
     */
    private String expectedResult;

    /**
     * 语气倾向
     */
    private String toneTendency;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否允许评论（0-否 1-是）
     */
    private Integer allowComment;

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

    public Integer getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Integer allowComment) {
        this.allowComment = allowComment;
    }
}
