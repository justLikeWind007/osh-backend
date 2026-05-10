package com.backstage.system.domain.assistant.dto;

/**
 * AI 助手工单查询请求 DTO
 *
 * @author backstage
 */
public class AssistantTicketQueryDTO {

    /**
     * 工单状态（PENDING、PROCESSING、RESOLVED、CLOSED）
     */
    private String status;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 搜索关键词（用于标题或内容模糊查询）
     */
    private String keyword;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
