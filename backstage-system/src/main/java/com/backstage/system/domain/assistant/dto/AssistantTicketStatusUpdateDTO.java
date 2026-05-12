package com.backstage.system.domain.assistant.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AI 助手工单状态更新请求 DTO
 *
 * @author backstage
 */
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

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
