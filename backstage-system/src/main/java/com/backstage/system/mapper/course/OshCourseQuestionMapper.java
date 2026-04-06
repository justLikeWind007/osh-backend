package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseQuestion;
import com.backstage.system.domain.course.vo.CourseQuestionAnswerItemVo;
import com.backstage.system.domain.course.vo.CourseQuestionListItemVo;
import com.backstage.system.request.CourseQuestionPageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface OshCourseQuestionMapper {

    int insertCourseQuestion(OshCourseQuestion question);

    OshCourseQuestion selectQuestionById(@Param("id") Long id);

    List<CourseQuestionListItemVo> selectSectionQuestionPage(CourseQuestionPageRequest request);

    List<CourseQuestionAnswerItemVo> selectQuestionAnswers(@Param("questionId") Long questionId);

    int updateQuestionReplyMeta(@Param("questionId") Long questionId,
                                @Param("lastReplyTime") Date lastReplyTime,
                                @Param("updateBy") String updateBy);
}
