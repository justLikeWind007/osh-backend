package com.backstage.system.request;

import com.backstage.common.request.PageRequest;

/**
 * 章节提问分页请求
 */
public class CourseQuestionPageRequest extends PageRequest {

    private Long courseId;
    private Long sectionId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }
}
