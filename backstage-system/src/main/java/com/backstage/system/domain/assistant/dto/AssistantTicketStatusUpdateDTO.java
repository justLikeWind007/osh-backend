package com.backstage.system.domain.assistant.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AI 助手工单状态更新请求 DTO
 *
 * @author backstage
 */
@Data
public class AssistantTicketStatusUpdateDTO {

    /**
     * 目标状态（PENDING、PROCESSING、RESOLVED、CLOSED）
     */
    @NotBlank(message = "状态不能为空")
    private String toStatus;

    /**
     * 处理说明
     */
    @Size(max = 1000, message = "处理说明不能超过1000个字符")
    private String remark;
}
