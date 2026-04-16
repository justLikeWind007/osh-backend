package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 问题信息 DTO
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "问题信息")
public class QuestionDTO {
    
    /** 问题 ID */
    @ApiModelProperty("问题 ID")
    private Long id;
    
    /** 课程 ID */
    @ApiModelProperty("课程 ID")
    private Long courseId;
    
    /** 章节 ID */
    @ApiModelProperty("章节 ID")
    private Long sectionId;
    
    /** 问题标题 */
    @ApiModelProperty("问题标题")
    private String questionTitle;
    
    /** 问题内容 */
    @ApiModelProperty("问题内容")
    private String questionContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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
}
