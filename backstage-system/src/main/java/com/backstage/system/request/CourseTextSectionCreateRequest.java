package com.backstage.system.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@ApiModel("文本小节创建请求")
public class CourseTextSectionCreateRequest {

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

    @ApiModelProperty(value = "文本内容", required = true, example = "这里是图文小节内容")
    @NotBlank(message = "文本内容不能为空")
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

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = StringUtils.trimToNull(textContent);
    }
}
