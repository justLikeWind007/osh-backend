package com.backstage.system.domain.vo.exam;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

public class UserExamRecordVo implements Serializable {
    private Long id;
    private Integer answer_status;
    private Integer read_status;
    private Integer score;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date created_time;

    private ExampaperInfoVo testpaper;

    public UserExamRecordVo() {}

    // Getter and Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnswer_status() {
        return answer_status;
    }

    public void setAnswer_status(Integer answer_status) {
        this.answer_status = answer_status;
    }

    public Integer getRead_status() {
        return read_status;
    }

    public void setRead_status(Integer read_status) {
        this.read_status = read_status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public ExampaperInfoVo getTestpaper() {
        return testpaper;
    }

    public void setTestpaper(ExampaperInfoVo testpaper) {
        this.testpaper = testpaper;
    }
}