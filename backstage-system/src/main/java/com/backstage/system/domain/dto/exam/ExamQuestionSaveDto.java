package com.backstage.system.domain.dto.exam;

import java.io.Serializable;

/**
 * Create / update a row in {@code osh_question}.
 */
public class ExamQuestionSaveDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** null = insert */
    private Long id;
    private Long examId;
    private String title;
    private Integer score;
    private String type;
    private String remark;
    /** JSON array string for radio/checkbox; null for other types */
    private String options;
    private String correctAnswer;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}
