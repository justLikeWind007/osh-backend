package com.backstage.system.service;

import com.backstage.system.domain.user.OshUser;
import com.backstage.system.service.course.CourseIndexDeleteMessage;
import com.backstage.system.service.course.CourseIndexUpsertMessage;

public interface OutboxEventService {

    void saveCourseIndexCreateEvent(Long courseId, CourseIndexUpsertMessage message, OshUser operator);

    void saveCourseIndexUpdateEvent(Long courseId, CourseIndexUpsertMessage message, OshUser operator);

    void saveCourseIndexDeleteEvent(Long courseId, CourseIndexDeleteMessage message, OshUser operator);
}
