package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseQuestion;
import com.backstage.system.domain.course.vo.CourseQuestionAnswerItemVo;
import com.backstage.system.domain.course.vo.CourseQuestionListItemVo;
import com.backstage.system.request.CourseQuestionPageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface OshCourseQuestionMapper {

    int insertCourseQuestion(OshCourseQuestion question);

    OshCourseQuestion selectQuestionById(@Param("id") Long id);

    List<CourseQuestionListItemVo> selectSectionQuestionPage(CourseQuestionPageRequest request);

    List<CourseQuestionAnswerItemVo> selectQuestionAnswers(@Param("questionId") Long questionId);

    int updateQuestionReplyMeta(@Param("questionId") Long questionId,
                                @Param("lastReplyTime") Date lastReplyTime,
                                @Param("updateBy") String updateBy);

    /**
     * 删除课程下所有问题
     *
     * @param courseId 课程ID
     * @return 删除行数
     */
    int deleteQuestionsByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计指定章节的问题数量
     *
     * @param sectionId 章节ID
     * @return 问题数量
     */
    int countQuestionsBySectionId(@Param("sectionId") Long sectionId);

    /**
     * 插入一个新问题
     *
     * @param params 问题参数Map
     * @return 插入行数
     */
    int insertQuestion(Map<String, Object> params);

    /**
     * 按课程和章节查询问题列表
     *
     * @param courseId 课程ID
     * @param sectionId 章节ID
     * @param status 问题状态
     * @return 问题列表
     */
    List<Map<String, Object>> selectQuestionsByCourseId(@Param("courseId") Long courseId,
                                                       @Param("sectionId") Long sectionId,
                                                       @Param("status") String status);

    /**
     * 更新问题
     *
     * @param params 问题参数Map
     * @return 更新行数
     */
    int updateQuestion(Map<String, Object> params);
}
