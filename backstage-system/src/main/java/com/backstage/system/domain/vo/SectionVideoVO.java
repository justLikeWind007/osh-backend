package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 章节视频信息 VO
 * 用于返回章节视频详情，包括视频URL、时长、封面等信息
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
@ApiModel(description = "章节视频信息")
public class SectionVideoVO {
    
    /** 章节 ID */
    @ApiModelProperty("章节ID")
    private Long sectionId;
    
    /** 章节标题 */
    @ApiModelProperty("章节标题")
    private String title;
    
    /** 课程 ID */
    @ApiModelProperty("课程ID")
    private Long courseId;
    
    /** 课程名称 */
    @ApiModelProperty("课程名称")
    private String courseName;
    
    /** 媒体资源URL */
    @ApiModelProperty("视频URL")
    private String mediaUrl;
    
    /** 视频封面图URL */
    @ApiModelProperty("视频封面图URL")
    private String cover;
    
    /** 视频时长（秒） */
    @ApiModelProperty("视频时长（秒）")
    private Integer duration;
    
    /** 视频时长文本（格式化后） */
    @ApiModelProperty("视频时长文本，如：30:00")
    private String durationText;
    
    /** 视频编码格式 */
    @ApiModelProperty("视频编码格式（h264/h265/av1）")
    private String videoCodec;
    
    /** 视频分辨率 */
    @ApiModelProperty("视频分辨率标识（720p/1080p/4k）")
    private String videoResolution;
    
    /** 视频文件大小（字节） */
    @ApiModelProperty("视频文件大小（字节）")
    private Long fileSize;
    
    /** 字幕文件URL */
    @ApiModelProperty("字幕文件URL")
    private String subtitleUrl;
    
    /** 章节类型 */
    @ApiModelProperty("章节类型：video-视频，audio-音频，text-图文，live-直播")
    private String type;
    
    /** 是否免费 */
    @ApiModelProperty("是否免费")
    private Boolean isFree;
    
    /** 用户是否有权限观看 */
    @ApiModelProperty("是否有观看权限")
    private Boolean hasAccess;
    
    /** 当前播放进度百分比（0-100） */
    @ApiModelProperty("当前播放进度百分比（0-100）")
    private Integer currentProgress;
    
    /** 上次播放位置（秒） */
    @ApiModelProperty("上次播放位置（秒）")
    private Integer lastPosition;
    
    /** 学习状态 */
    @ApiModelProperty("学习状态：0-未开始 1-学习中 2-有疑问 3-已完成")
    private Integer status;
    
    /** 是否已完成 */
    @ApiModelProperty("是否已完成")
    private Boolean isCompleted;
    
    /** 关联考试ID */
    @ApiModelProperty("关联考试ID（学完跳转答题）")
    private Long examId;

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public String getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    public void setSubtitleUrl(String subtitleUrl) {
        this.subtitleUrl = subtitleUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public Boolean getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(Boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    public Integer getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(Integer currentProgress) {
        this.currentProgress = currentProgress;
    }

    public Integer getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Integer lastPosition) {
        this.lastPosition = lastPosition;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }
}
