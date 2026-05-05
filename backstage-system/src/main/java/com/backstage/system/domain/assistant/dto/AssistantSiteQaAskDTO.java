package com.backstage.system.domain.assistant.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * AI 助手站点问答请求 DTO
 *
 * @author backstage
 */
@Data
public class AssistantSiteQaAskDTO {

    /**
     * 用户提问内容
     */
    @NotBlank(message = "问题不能为空")
    private String question;
}
