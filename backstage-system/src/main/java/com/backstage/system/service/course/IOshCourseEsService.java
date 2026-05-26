package com.backstage.system.service.course;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import com.backstage.system.request.CourseSearchRequest;

public interface IOshCourseEsService {

    PageResponse<CourseSearchLoginVo> searchCourses(CourseSearchRequest request, Long userId);

    int syncAllCoursesToEs();

    int syncAllCoursesToEsWithoutStatusFilter();

    /**
     * 将单门课程同步写入 ES（含待审核状态），供列表/审核页即时可见。
     */
    void upsertCourseById(Long courseId);
}
