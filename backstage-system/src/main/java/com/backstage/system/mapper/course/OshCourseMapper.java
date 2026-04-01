package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程信息 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-01-XX
 */
@Mapper
public interface OshCourseMapper
{
    /**
     * 查询课程信息
     *
     * @param id 课程 ID
     * @return 课程信息
     */
    OshCourse selectCourseById(Long id);

    /**
     * 根据 appid 和专栏 ID 查询课程列表
     *
     * @param columnId 专栏 ID
     * @return 课程列表
     */
    List<OshCourse> selectCourseList(@Param("columnId") Long columnId);

    /**
     * 根据条件查询课程列表（支持多标签筛选 + 关键字搜索 + 动态排序）
     *
     * @param params 查询参数：tagIds(List), keyword(String), sortBy(String), sortOrder(String)
     * @return 课程列表
     */
    List<OshCourse> selectCourseListByCondition(Map<String, Object> params);

    /**
     * 新增课程
     *
     * @param course 课程信息
     * @return 结果
     */
    int insertCourse(OshCourse course);

    /**
     * 修改课程信息
     *
     * @param course 课程信息
     * @return 结果
     */
    int updateCourse(OshCourse course);

    /**
     * 删除课程
     *
     * @param id 课程 ID
     * @return 结果
     */
    int deleteCourseById(Long id);

    /**
     * 批量删除课程
     *
     * @param ids 需要删除的数据 ID
     * @return 结果
     */
    int deleteCourseByIds(Long[] ids);
    
    /**
     * 增加课程收藏计数
     *
     * @param params 包含 courseId
     * @return 结果
     */
    int incrementFavaCount(Map<String, Object> params);
    
    /**
     * 减少课程收藏计数
     *
     * @param params 包含 courseId
     * @return 结果
     */
    int decrementFavaCount(Map<String, Object> params);
    
    /**
     * 更新课程章节数量
     *
     * @param params 参数（包含 id 和 subCount）
     * @return 结果
     */
    int updateCourseSubCount(Map<String, Object> params);
}
