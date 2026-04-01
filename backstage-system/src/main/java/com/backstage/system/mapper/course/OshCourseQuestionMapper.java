package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface OshCourseQuestionMapper {

    int insertCourseQuestion(OshCourseQuestion question);

    OshCourseQuestion selectQuestionById(@Param("id") Long id);

    int updateQuestionReplyMeta(@Param("questionId") Long questionId,
                                @Param("lastReplyTime") Date lastReplyTime,
                                @Param("updateBy") String updateBy);
}
