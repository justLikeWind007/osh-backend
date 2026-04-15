package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 章节学习进度 VO
 * 用于返回用户对某章节的学习进度信息
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
@ApiModel(description = "章节学习进度")
public class SectionProgressVO {
    
    /** 章节 ID */
    @ApiModelProperty("章节ID")
    private Long sectionId;
    
    /** 课程 ID */
    @ApiModelProperty("课程ID")
    private Long courseId;
    
    /** 学习状态 */
    @ApiModelProperty("学习状态：0-未开始 1-学习中 2-有疑问 3-已完成")
    private Integer status;
    
    /** 进度百分比（0-100） */
    @ApiModelProperty("进度百分比（0-100）")
    private Integer progress;
    
    /** 上次播放位置（秒） */
    @ApiModelProperty("上次播放位置（秒）")
    private Integer lastPosition;
    
    /** 累计学习时长（秒） */
    @ApiModelProperty("累计学习时长（秒）")
    private Integer learnTime;
    
    /** 观看次数 */
    @ApiModelProperty("观看次数")
    private Integer watchCount;
    
    /** 是否已完成 */
    @ApiModelProperty("是否已完成")
    private Boolean isCompleted;
    
    /** 首次完成时间 */
    @ApiModelProperty("首次完成时间")
    private Date completeTime;
    
    /** 首次学习时间 */
    @ApiModelProperty("首次学习时间")
    private Date createTime;
    
    /** 最近更新时间 */
    @ApiModelProperty("最近更新时间")
    private Date updateTime;

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Integer lastPosition) {
        this.lastPosition = lastPosition;
    }

    public Integer getLearnTime() {
        return learnTime;
    }

    public void setLearnTime(Integer learnTime) {
        this.learnTime = learnTime;
    }

    public Integer getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(Integer watchCount) {
        this.watchCount = watchCount;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
