package com.backstage.system.mapper.course;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 用户课程学习进度 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
public interface OshUserCourseProgressMapper {
    
    /**
     * 查询用户课程学习进度（课程级别）
     */
    Map<String, Object> selectProgressByUserIdAndCourseId(
        @Param("userId") Long userId,
        @Param("courseId") Long courseId
    );
    
    /**
     * 查询用户章节学习进度（章节级别）
     * 
     * @param userId 用户ID
     * @param sectionId 章节ID
     * @return 章节学习进度
     */
    Map<String, Object> selectSectionProgress(
        @Param("userId") Long userId,
        @Param("sectionId") Long sectionId
    );
    
    /**
     * 新增章节学习进度
     */
    int insertSectionProgress(Map<String, Object> params);
    
    /**
     * 更新章节学习进度
     */
    int updateSectionProgress(Map<String, Object> params);
    
    /**
     * 更新章节学习状态（标记完成/有疑问等）
     * 
     * @param params 包含userId、sectionId、status等
     * @return 影响行数
     */
    int updateSectionStatus(Map<String, Object> params);
    
    /**
     * 增加观看次数
     * 
     * @param userId 用户ID
     * @param sectionId 章节ID
     * @return 影响行数
     */
    int incrementWatchCount(
        @Param("userId") Long userId,
        @Param("sectionId") Long sectionId
    );
    
    /**
     * 新增学习进度（课程级别）
     */
    int insertProgress(Map<String, Object> params);
    
    /**
     * 更新学习进度（课程级别）
     */
    int updateProgress(Map<String, Object> params);
    
    /**
     * 删除学习进度（按课程 ID）
     */
    int deleteProgressByCourseId(@Param("courseId") Long courseId);
}
