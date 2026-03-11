package com.backstage.system.service;

import com.backstage.system.domain.course.SysCourse;

import java.util.List;

/**
 * 课程信息 Service 接口
 * 
 * @author ruoyi
 * @date 2026-01-XX
 */
public interface ISysCourseService 
{
    /**
     * 查询课程详情
     * 
     * @param id 课程 ID
     * @return 课程信息
     */
    SysCourse selectCourseById(Long id);

    /**
     * 查询课程列表
     *
     * @param columnId 专栏 ID
     * @return 课程集合
     */
    List<SysCourse> selectCourseList(Long columnId);

    /**
     * 新增课程
     * 
     * @param course 课程信息
     * @return 结果
     */
    int insertCourse(SysCourse course);

    /**
     * 修改课程
     * 
     * @param course 课程信息
     * @return 结果
     */
    int updateCourse(SysCourse course);

    /**
     * 批量删除课程
     * 
     * @param ids 需要删除的课程 ID
     * @return 结果
     */
    int deleteCourseByIds(Long[] ids);

    /**
     * 删除课程
     * 
     * @param id 课程 ID
     * @return 结果
     */
    int deleteCourseById(Long id);
}
