package com.backstage.system.domain.assistant.vo;

/**
 * 反馈标签响应 VO
 *
 * @author backstage
 */
public class AssistantFeedbackTagVO {

    /**
     * 标签 ID
     */
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
}
