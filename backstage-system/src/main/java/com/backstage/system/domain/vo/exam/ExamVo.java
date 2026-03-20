package com.backstage.system.domain.vo.exam;

import java.io.Serializable;

/**
 * 考场列表返回对象
 */
public class ExamVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;              // 考场ID
    private String title;         // 试卷标题
    private Integer total_score;  // 总分
    private Integer pass_score;   // 及格分
    private Integer expire;       // 时长(分钟)
    private Integer question_count; // 题目数量

    public ExamVo() {}

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

    public Integer getQuestion_count() { return question_count; }
    public void setQuestion_count(Integer question_count) { this.question_count = question_count; }
}