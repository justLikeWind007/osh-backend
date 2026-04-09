package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 章节创建DTO
 *
 * @author ruoyi
 * @date 2026-03-27
 */
@ApiModel(description = "章节创建DTO")
public class SectionCreateDTO {

    @ApiModelProperty("章节标题")
    @NotBlank(message = "章节标题不能为空")
    private String title;

    @ApiModelProperty("父级ID（0=章 >0=节）")
    private Long parentId = 0L;

    @ApiModelProperty("排序序号")
    private Integer sort = 0;

    @ApiModelProperty("是否免费试看：0-否 1-是")
    private Integer isFree = 0;

    @ApiModelProperty("小节类型：video/audio/text/live")
    private String sectionType;

    @ApiModelProperty("视频时长（秒）")
    private Integer duration;

    @ApiModelProperty("媒体资源URL")
    private String mediaUrl;

    @ApiModelProperty("视频封面图URL")
    private String cover;

    @ApiModelProperty("视频编码格式")
    private String videoCodec;

    @ApiModelProperty("视频比特率（kbps）")
    private Integer videoBitrate;

    @ApiModelProperty("视频分辨率标识")
    private String videoResolution;

    @ApiModelProperty("字幕文件URL")
    private String subtitleUrl;

    @ApiModelProperty("状态：0-隐藏 1-发布")
    private Integer status = 1;

    @ApiModelProperty("关联考试ID（学完跳转答题）")
    private Long examId;

    @ApiModelProperty("资料列表")
    private List<SectionMaterialDTO> materials;

    // ==================== Getter/Setter ====================

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

    public List<SectionMaterialDTO> getMaterials() {
        return materials;
    }

    public void setMaterials(List<SectionMaterialDTO> materials) {
        this.materials = materials;
    }
}
