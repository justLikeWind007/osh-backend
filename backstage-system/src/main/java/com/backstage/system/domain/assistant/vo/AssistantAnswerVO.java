package com.backstage.system.domain.assistant.vo;

import lombok.Data;

/**
 * AI 助手问答响应 VO
 *
 * @author backstage
 */
@Data
public class AssistantAnswerVO {

    /**
     * 问答模式（site：站点问答、course：课程问答）
     */
    private String mode;

    /**
     * 课程 ID（仅课程问答模式）
     */
    private Long courseId;

    /**
     * 课程名称（仅课程问答模式）
     */
    private String courseName;

    /**
     * 用户提问内容
     */
    private String question;

    /**
     * AI 回答内容
     */
    private String answer;

    /**
     * 回答类型（mock：模拟回答、rag：RAG 检索回答、llm：大模型直接回答）
     */
    private String answerType;
}
