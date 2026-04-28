package com.backstage.system.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@ApiModel("视频小节创建请求")
public class CourseVideoSectionCreateRequest {

    @ApiModelProperty(value = "课程ID", required = true, example = "100")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @ApiModelProperty(value = "父章节ID，必须是一级章节ID", required = true, example = "10")
    @NotNull(message = "父章节ID不能为空")
    private Long parentId;

    @ApiModelProperty(value = "小节标题", required = true, example = "第一节")
    @NotBlank(message = "小节标题不能为空")
    private String title;

    @ApiModelProperty(value = "排序值，越小越靠前", required = true, example = "1")
    @NotNull(message = "排序不能为空")
    @PositiveOrZero(message = "排序不能小于0")
    private Integer sort;

    @ApiModelProperty(value = "是否免费：0-否，1-是；不传默认0", example = "0")
    @PositiveOrZero(message = "是否免费标记不能小于0")
    private Integer freeFlag;

    @ApiModelProperty(value = "视频时长，单位秒", example = "600")
    @PositiveOrZero(message = "时长不能小于0")
    private Integer duration;

    @ApiModelProperty(value = "视频OSS地址", required = true, example = "https://oss.example.com/video.mp4")
    private String mediaUrl;

    @ApiModelProperty(value = "封面图地址", example = "https://oss.example.com/cover.png")
    private String cover;

    @ApiModelProperty(value = "视频描述", example = "1080P 视频")
    private String videoDesc;

    @ApiModelProperty(value = "视频配套文本内容", example = "这里是视频对应的补充说明")
    private String textContent;

    @ApiModelProperty(value = "文件大小，单位字节", required = true, example = "123456")
    @NotNull(message = "文件大小不能为空")
    @PositiveOrZero(message = "文件大小不能小于0")
    private Long fileSize;

    @ApiModelProperty(value = "小节ID，有值则更新，无值则新增")
    private Long id;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = StringUtils.trimToNull(title);
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getFreeFlag() {
        return freeFlag;
    }

    public void setFreeFlag(Integer freeFlag) {
        this.freeFlag = freeFlag;
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
        this.mediaUrl = StringUtils.trimToNull(mediaUrl);
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = StringUtils.trimToNull(cover);
    }

    public String getVideoDesc() {
        return videoDesc;
    }

    public void setVideoDesc(String videoDesc) {
        this.videoDesc = StringUtils.trimToNull(videoDesc);
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = StringUtils.trimToNull(textContent);
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

}
