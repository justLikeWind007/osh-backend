package com.backstage.system.service;

public interface IOshCourseCollectionService {

    void collectCourse(Long userId, String operator, Long courseId);

    void removeCourseCollection(Long userId, String operator, Long courseId);
}
