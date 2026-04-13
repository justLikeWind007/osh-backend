package com.backstage.system.request;

import javax.validation.constraints.NotNull;

/**
 * 课程修改请求
 */
public class CourseUpdateRequest extends CourseCreateRequest {

    @NotNull(message = "课程id不能为空")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
