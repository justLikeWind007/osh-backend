package com.backstage.system.domain.comment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "课程评论新增参数", description = "专栏课程评论/回复请求实体")
public class CourseCommentAddDTO {

    @ApiModelProperty(value = "专栏ID", required = true)
    private Long columnId;

    @ApiModelProperty(value = "课程ID", required = true)
    private Long courseId;

    @ApiModelProperty(value = "父评论ID，0表示一级评论", example = "0")
    private Long parentId;

    @ApiModelProperty(value = "评论内容", required = true)
    private String content;

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
