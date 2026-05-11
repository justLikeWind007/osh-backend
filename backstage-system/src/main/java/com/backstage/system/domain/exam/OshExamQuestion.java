package com.backstage.system.domain.exam;

import java.io.Serializable;
import java.util.Date;

/**
 * Exam paper question row (<code>osh_question</code>).
 */
public class OshExamQuestion implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long examId;
    private String title;
    private Integer score;
    private String type;
    private String remark;
    private String options;
    private String correctAnswer;
    private Integer deleteFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

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
    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
