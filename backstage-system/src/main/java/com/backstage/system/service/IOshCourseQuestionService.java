package com.backstage.system.service;

import com.backstage.system.domain.course.vo.CourseQuestionAnswerItemVo;
import com.backstage.system.domain.course.vo.CourseQuestionListItemVo;
import com.backstage.system.request.CourseQuestionAnswerRequest;
import com.backstage.system.request.CourseSectionQuestionListRequest;
import com.backstage.system.request.CourseSectionQuestionRequest;

import java.util.List;

public interface IOshCourseQuestionService {

    Long submitQuestion(Long userId, String operator, CourseSectionQuestionRequest request);

    Long answerQuestion(Long userId, String operator, CourseQuestionAnswerRequest request);

    List<CourseQuestionListItemVo> listSectionQuestions(Long userId, CourseSectionQuestionListRequest request);

    List<CourseQuestionAnswerItemVo> listQuestionAnswers(Long questionId);
}
