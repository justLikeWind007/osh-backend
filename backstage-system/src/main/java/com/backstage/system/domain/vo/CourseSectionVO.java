package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 课程章节 VO
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "课程章节 VO")
public class CourseSectionVO {
    
    /** 章节 ID */
    @ApiModelProperty("章节 ID")
    private Long id;
    
    /** 章节标题 */
    @ApiModelProperty("章节标题")
    private String title;
    
    /** 章节类型 */
    @ApiModelProperty("章节类型：video-视频，text-图文，live-直播")
    private String sectionType;
    
    /** 时长 (秒) */
    @ApiModelProperty("时长 (秒)")
    private Integer duration;
    
    /** 是否免费 */
    @ApiModelProperty("是否免费")
    private Boolean isFree;
    
    /** 是否已学习 */
    @ApiModelProperty("是否已学习")
    private Boolean isLearned;
    
    /** 是否有问题 */
    @ApiModelProperty("是否有问题")
    private Boolean hasQuestion;
    
    /** 问题数量 */
    @ApiModelProperty("问题数量")
    private Integer questionCount;
    
    /** 是否锁定 */
    @ApiModelProperty("是否锁定 (未购买)")
    private Boolean locked;

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

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Boolean getFree() {
        return isFree;
    }

    public void setFree(Boolean free) {
        this.isFree = free;
    }

    public Boolean getIsLearned() {
        return isLearned;
    }

    public void setIsLearned(Boolean isLearned) {
        this.isLearned = isLearned;
    }

    public void setLearned(Boolean learned) {
        this.isLearned = learned;
    }

    public Boolean getHasQuestion() {
        return hasQuestion;
    }

    public void setHasQuestion(Boolean hasQuestion) {
        this.hasQuestion = hasQuestion;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
