package com.backstage.system.domain.exam;

public class OshUserExamRecord {
    private Long id;
    private Integer score;         // 最终得分
    private Integer answer_status; // 0-考试中，1-已完成
    private Integer read_status;   // 0-未批改，1-已批改
    private String answer_json;    // 存储用户提交的完整答案 JSON

    // Getters and Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Integer getAnswer_status() { return answer_status; }
    public void setAnswer_status(Integer answer_status) { this.answer_status = answer_status; }
    public Integer getRead_status() { return read_status; }
    public void setRead_status(Integer read_status) { this.read_status = read_status; }
    public String getAnswer_json() { return answer_json; }
    public void setAnswer_json(String answer_json) { this.answer_json = answer_json; }
}