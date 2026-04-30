package com.backstage.system.mapper.course;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程评价 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
public interface OshCourseReviewMapper {
    
    /**
     * 查询评价列表
     */
    List<Map<String, Object>> selectReviewsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 统计评价数量（按评分）
     */
    Map<String, Object> countReviewsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 新增评价
     */
    int insertReview(Map<String, Object> params);
    
    /**
     * 修改评价
     */
    int updateReview(Map<String, Object> params);
    
    /**
     * 删除评价
     */
    int deleteReviewById(@Param("id") Long id);
    
    /**
     * 批量删除评价
     */
    int deleteReviewsByCourseId(@Param("courseId") Long courseId);
}
