package com.backstage.system.domain.vo.exam;

import java.io.Serializable;

public class ExampaperInfoVo implements Serializable {
    private Long id;
    private String title;
    private Integer total_score;
    private Integer pass_score;
    private Integer expire;
    private Integer question_count;

    // 无参构造函数
    public ExampaperInfoVo() {}

    // Getter and Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotal_score() {
        return total_score;
    }

    public void setTotal_score(Integer total_score) {
        this.total_score = total_score;
    }

    public Integer getPass_score() {
        return pass_score;
    }

    public void setPass_score(Integer pass_score) {
        this.pass_score = pass_score;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public Integer getQuestion_count() {
        return question_count;
    }

    public void setQuestion_count(Integer question_count) {
        this.question_count = question_count;
    }
}