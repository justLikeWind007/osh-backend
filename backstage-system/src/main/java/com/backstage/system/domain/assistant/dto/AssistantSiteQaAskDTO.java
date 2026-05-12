package com.backstage.system.domain.assistant.dto;

import javax.validation.constraints.NotBlank;

/**
 * AI 助手站点问答请求 DTO
 *
 * @author backstage
 */
public class AssistantSiteQaAskDTO {

    /**
     * 用户提问内容
     */
    @NotBlank(message = "问题不能为空")
    private String question;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
