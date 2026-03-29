package com.backstage.system.mapper.course;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程问答 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
public interface OshCourseQuestionMapper {
    
    /**
     * 查询问题列表
     */
    List<Map<String, Object>> selectQuestionsByCourseId(
        @Param("courseId") Long courseId,
        @Param("sectionId") Long sectionId,
        @Param("status") String status
    );
    
    /**
     * 查询问题详情
     */
    Map<String, Object> selectQuestionById(@Param("id") Long id);
    
    /**
     * 统计问题数量
     */
    int countQuestionsBySectionId(@Param("sectionId") Long sectionId);
    
    /**
     * 新增问题
     */
    int insertQuestion(Map<String, Object> params);
    
    /**
     * 修改问题
     */
    int updateQuestion(Map<String, Object> params);
    
    /**
     * 删除问题
     */
    int deleteQuestionById(@Param("id") Long id);
    
    /**
     * 批量删除问题
     */
    int deleteQuestionsByCourseId(@Param("courseId") Long courseId);
}
