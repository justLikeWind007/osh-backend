package com.backstage.system.service.course;

public interface CourseIndexKafkaProducer {

    void sendCourseIndexCreate(CourseIndexUpsertMessage message);

    void sendCourseIndexUpdate(CourseIndexUpsertMessage message);
}
