package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 课程问答 VO
 * 对应表: osh_question_answer_question
 * 
 * @author ruoyi
 * @date 2026-03-31
 */
@ApiModel(description = "课程问答 VO")
public class CourseQuestionVO {
    
    /** 问题 ID */
    @ApiModelProperty("问题 ID")
    private Long id;
    
    /** 资源类型: 1=课程问答, 2=章节问答 */
    @ApiModelProperty("资源类型: 1=课程问答, 2=章节问答")
    private Integer resourceType;
    
    /** 资源ID (课程ID或章节ID) */
    @ApiModelProperty("资源ID (课程ID或章节ID)")
    private Long resourceNo;
    
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
    
    /** 状态: 0=待回答, 1=已回答, 2=已解决 */
    @ApiModelProperty("状态: 0=待回答, 1=已回答, 2=已解决")
    private Integer status;
    
    /** 浏览数 */
    @ApiModelProperty("浏览数")
    private Integer viewCount;
    
    /** 关注数 */
    @ApiModelProperty("关注数")
    private Integer followCount;
    
    /** 创建时间 */
    @ApiModelProperty("创建时间")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(Long resourceNo) {
        this.resourceNo = resourceNo;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
