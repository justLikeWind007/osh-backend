package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 视频上传结果VO
 *
 * @author ruoyi
 * @date 2026-03-27
 */
@ApiModel(description = "视频上传结果VO")
public class VideoUploadVO {

    @ApiModelProperty("视频文件URL")
    private String videoUrl;

    @ApiModelProperty("视频封面图URL")
    private String coverUrl;

    @ApiModelProperty("视频时长（秒）")
    private Integer duration;

    @ApiModelProperty("视频编码格式")
    private String videoCodec;

    @ApiModelProperty("视频比特率（kbps）")
    private Integer videoBitrate;

    @ApiModelProperty("视频分辨率标识")
    private String videoResolution;

    @ApiModelProperty("文件大小（字节）")
    private Long fileSize;

    @ApiModelProperty("文件原始名称")
    private String originalFileName;

    @ApiModelProperty("视频资源ID（新插入的视频记录ID）")
    private Long videoId;

    @ApiModelProperty("视频在章节中的排序序号")
    private Integer videoOrder;

    // ==================== Getter/Setter ====================

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Integer getVideoOrder() {
        return videoOrder;
    }

    public void setVideoOrder(Integer videoOrder) {
        this.videoOrder = videoOrder;
    }
}