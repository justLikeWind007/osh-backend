package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
}

