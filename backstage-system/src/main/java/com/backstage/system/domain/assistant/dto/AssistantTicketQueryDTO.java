package com.backstage.system.domain.assistant.dto;

import lombok.Data;

/**
 * AI 助手工单查询请求 DTO
 *
 * @author backstage
 */
@Data
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
}
