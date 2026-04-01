package com.backstage.system.service;

import com.backstage.system.request.CourseQuestionAnswerRequest;
import com.backstage.system.request.CourseSectionQuestionRequest;

public interface IOshCourseQuestionService {

    Long submitQuestion(Long userId, String operator, CourseSectionQuestionRequest request);

    Long answerQuestion(Long userId, String operator, CourseQuestionAnswerRequest request);
}
