package com.backstage.system.domain.assistant.vo;

/**
 * AI 助手问答响应 VO
 *
 * @author backstage
 */
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }
}
