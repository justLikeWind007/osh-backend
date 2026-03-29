package com.backstage.system.domain.course;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 课程章节实体对象 osh_course_section
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
public class OshCourseSection {
    private static final long serialVersionUID = 1L;

    /** 章节 ID */
    private Long id;

    /** 课程 ID */
    private Long courseId;

    /** 章节标题 */
    private String title;

    /** 父章节 ID（0 表示一级章节） */
    private Long parentId;

    /** 排序序号 */
    private Integer sort;

    /** 是否免费（0-付费，1-免费） */
    private Integer isFree;

    /** 章节类型（chapter-章，section-节） */
    private String sectionType;

    /** 视频时长（秒） */
    private Integer duration;

    /** 视频 URL */
    private String mediaUrl;

    /** 封面图 URL */
    private String cover;

    /** 视频编码格式 */
    private String videoCodec;

    /** 视频比特率 */
    private Integer videoBitrate;

    /** 视频分辨率 */
    private String videoResolution;

    /** 字幕 URL */
    private String subtitleUrl;

    /** 状态（0-下架，1-上架） */
    private Integer status;

    /** 关联考试 ID */
    private Long examId;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
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

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public Integer getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(Integer videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public String getVideoResolution() {
        return videoResolution;
    }

    public void setVideoResolution(String videoResolution) {
        this.videoResolution = videoResolution;
    }

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    public void setSubtitleUrl(String subtitleUrl) {
        this.subtitleUrl = subtitleUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "OshCourseSection{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", parentId=" + parentId +
                ", sort=" + sort +
                ", isFree=" + isFree +
                ", sectionType='" + sectionType + '\'' +
                ", duration=" + duration +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", cover='" + cover + '\'' +
                ", videoCodec='" + videoCodec + '\'' +
                ", videoBitrate=" + videoBitrate +
                ", videoResolution='" + videoResolution + '\'' +
                ", subtitleUrl='" + subtitleUrl + '\'' +
                ", status=" + status +
                ", examId=" + examId +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
