package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseSection;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程章节 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@Mapper
public interface OshCourseSectionMapper {
    
    /**
     * 查询章节列表
     */
    @MapKey("id")
    List<Map<String, Object>> selectSectionsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 查询章节详情
     */

    Map<String, Object> selectSectionById(@Param("sectionId") Long sectionId);
    
    /**
     * 查询章节视频详情（包含视频相关信息）
     * 
     * @param sectionId 章节 ID
     * @return 章节视频信息
     */
    @MapKey("id")
    Map<String, Object> selectSectionVideoById(@Param("sectionId") Long sectionId);
    
    /**
     * 统计章节数量
     */
    int countSectionsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 新增章节
     */
    int insertSection(Map<String, Object> params);
    
    /**
     * 新增章节（使用实体对象）
     * @param section 章节实体
     * @return 结果
     */
    int insertSectionEntity(OshCourseSection section);
    
    /**
     * 修改章节
     */
    int updateSection(Map<String, Object> params);
    
    /**
     * 删除章节
     */
    int deleteSectionById(@Param("id") Long id);
    
    /**
     * 批量删除章节
     */
    int deleteSectionsByCourseId(@Param("courseId") Long courseId);
}
