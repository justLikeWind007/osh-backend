package com.backstage.system.service.course;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import com.backstage.system.request.CourseSearchRequest;

public interface IOshCourseEsService {

    PageResponse<CourseSearchLoginVo> searchCourses(CourseSearchRequest request, Long userId);

    int syncAllCoursesToEs();

    int syncAllCoursesToEsWithoutStatusFilter();
}
