package com.backstage.system.request;

import javax.validation.constraints.NotNull;

/**
 * 课程收藏请求
 */
public class CourseCollectionRequest {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
