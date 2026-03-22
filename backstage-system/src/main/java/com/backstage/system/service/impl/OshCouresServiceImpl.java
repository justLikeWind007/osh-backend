package com.backstage.system.service.impl;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.service.IOshCouresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程信息 Service 业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-XX
 */
@Service
public class OshCouresServiceImpl implements IOshCouresService 
{
    @Autowired
    private OshCourseMapper oshCouresMapper;

    /**
     * 查询课程详情
     * 
     * @param id 课程 ID
     * @return 课程信息
     */
    @Override
    public OshCourse selectCourseById(Long id)
    {
        return oshCouresMapper.selectCourseById(id);
    }

    /**
     * 查询课程列表
     *
     * @param columnId 专栏 ID
     * @return 课程集合
     */
    @Override
    public List<OshCourse> selectCourseList(Long columnId)
    {
        return oshCouresMapper.selectCourseList(columnId);
    }

    /**
     * 新增课程
     * 
     * @param course 课程信息
     * @return 结果
     */
    @Override
    public int insertCourse(OshCourse course)
    {
        return oshCouresMapper.insertCourse(course);
    }

    /**
     * 修改课程
     * 
     * @param course 课程信息
     * @return 结果
     */
    @Override
    public int updateCourse(OshCourse course)
    {
        return oshCouresMapper.updateCourse(course);
    }

    /**
     * 批量删除课程
     * 
     * @param ids 需要删除的课程 ID
     * @return 结果
     */
    @Override
    public int deleteCourseByIds(Long[] ids)
    {
        return oshCouresMapper.deleteCourseByIds(ids);
    }

    /**
     * 删除课程
     * 
     * @param id 课程 ID
     * @return 结果
     */
    @Override
    public int deleteCourseById(Long id)
    {
        return oshCouresMapper.deleteCourseById(id);
    }
}