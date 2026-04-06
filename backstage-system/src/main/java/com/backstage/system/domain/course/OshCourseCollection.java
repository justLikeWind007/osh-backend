package com.backstage.system.domain.course;

import com.backstage.common.core.domain.BaseEntity;

/**
 * 课程收藏对象 osh_course_collection
 */
public class OshCourseCollection extends BaseEntity {

    private Long id;
    private Long userId;
    private Long courseId;
    private Integer deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
