package com.backstage.system.service;

import com.backstage.system.domain.user.OshUser;
import com.backstage.system.service.course.CourseIndexDeleteMessage;
import com.backstage.system.service.course.CourseIndexUpsertMessage;
import com.backstage.system.service.tool.ToolIndexDeleteMessage;
import com.backstage.system.service.tool.ToolIndexMessage;

public interface OutboxEventService {

    void saveCourseIndexEvent(Long courseId, CourseIndexUpsertMessage message, OshUser operator);

    void saveCourseIndexDeleteEvent(Long courseId, CourseIndexDeleteMessage message, OshUser operator);

    void saveToolIndexEvent(Long toolId, ToolIndexMessage message, OshUser operator);

    void saveToolIndexEvent(Long toolId, ToolIndexMessage message, String operator);

    void saveToolIndexDeleteEvent(Long toolId, ToolIndexDeleteMessage message, OshUser operator);
}
