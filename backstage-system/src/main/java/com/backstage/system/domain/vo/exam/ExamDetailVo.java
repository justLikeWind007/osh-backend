package com.backstage.system.domain.vo.exam;

import java.io.Serializable;
import java.util.List;

public class ExamDetailVo implements Serializable {
    private Long id;
    private String title;
    private Integer total_score;
    private Integer pass_score;
    private Integer expire;
    private List<QuestionVo> testpaper_questions; // 题目列表
    private Long user_test_id; // 用户考试记录ID（暂给模拟值）

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getTotal_score() { return total_score; }
    public void setTotal_score(Integer total_score) { this.total_score = total_score; }
    public Integer getPass_score() { return pass_score; }
    public void setPass_score(Integer pass_score) { this.pass_score = pass_score; }
    public Integer getExpire() { return expire; }
    public void setExpire(Integer expire) { this.expire = expire; }
    public List<QuestionVo> getTestpaper_questions() { return testpaper_questions; }
    public void setTestpaper_questions(List<QuestionVo> testpaper_questions) { this.testpaper_questions = testpaper_questions; }
    public Long getUser_test_id() { return user_test_id; }
    public void setUser_test_id(Long user_test_id) { this.user_test_id = user_test_id; }
}