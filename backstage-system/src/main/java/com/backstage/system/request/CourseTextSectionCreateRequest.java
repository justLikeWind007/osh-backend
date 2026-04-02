package com.backstage.system.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@ApiModel("图文小节创建请求")
public class CourseTextSectionCreateRequest {

    @ApiModelProperty(value = "课程ID", required = true, example = "100")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @ApiModelProperty(value = "父章节ID，必须是一级章节ID", required = true, example = "10")
    @NotNull(message = "父章节ID不能为空")
    private Long parentId;

    @ApiModelProperty(value = "小节标题", required = true, example = "课前须知")
    @NotBlank(message = "小节标题不能为空")
    private String title;

    @ApiModelProperty(value = "排序值，越小越靠前", required = true, example = "2")
    @NotNull(message = "排序不能为空")
    @PositiveOrZero(message = "排序不能小于0")
    private Integer sort;

    @ApiModelProperty(value = "是否免费：0-否，1-是；不传默认0", example = "0")
    @PositiveOrZero(message = "是否免费标记不能小于0")
    private Integer freeFlag;

    @ApiModelProperty(value = "封面图地址", example = "https://oss.example.com/text-cover.png")
    private String cover;

    @ApiModelProperty(value = "图文内容", required = true, example = "这里是图文内容")
    @NotBlank(message = "图文内容不能为空")
    private String textContent;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = StringUtils.trimToNull(cover);
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = StringUtils.trimToNull(textContent);
    }
}
