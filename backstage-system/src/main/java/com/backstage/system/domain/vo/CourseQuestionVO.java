package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 课程问答 VO
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "课程问答 VO")
public class CourseQuestionVO {
    
    /** 问题 ID */
    @ApiModelProperty("问题 ID")
    private Long id;
    
    /** 章节标题 */
    @ApiModelProperty("章节标题")
    private String sectionTitle;
    
    /** 问题标题 */
    @ApiModelProperty("问题标题")
    private String questionTitle;
    
    /** 问题内容 */
    @ApiModelProperty("问题内容")
    private String questionContent;
    
    /** 提问者姓名 */
    @ApiModelProperty("提问者姓名")
    private String askerName;
    
    /** 回答内容 */
    @ApiModelProperty("回答内容")
    private String answerContent;
    
    /** 回答者姓名 */
    @ApiModelProperty("回答者姓名")
    private String answererName;
    
    /** 回答时间 */
    @ApiModelProperty("回答时间")
    private Date answerTime;
    
    /** 状态 */
    @ApiModelProperty("状态：pending-待回答，answered-已回答，resolved-已解决")
    private String status;
    
    /** 点赞数 */
    @ApiModelProperty("点赞数")
    private Integer likeCount;
    
    /** 是否置顶 */
    @ApiModelProperty("是否置顶")
    private Boolean isTop;
    
    /** 创建时间 */
    @ApiModelProperty("创建时间")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAskerName() {
        return askerName;
    }

    public void setAskerName(String askerName) {
        this.askerName = askerName;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getAnswererName() {
        return answererName;
    }

    public void setAnswererName(String answererName) {
        this.answererName = answererName;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
