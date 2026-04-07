package com.backstage.system.service.common.impl;

import java.util.List;
import com.backstage.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backstage.system.mapper.common.OshRecommendCoursesMapper;
import com.backstage.system.domain.common.OshRecommendCourses;
import com.backstage.system.service.common.IOshRecommendCoursesService;

/**
 * 推荐列内容Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-10
 */
@Service
public class OshRecommendCoursesServiceImpl implements IOshRecommendCoursesService 
{
    @Autowired
    private OshRecommendCoursesMapper oshRecommendCoursesMapper;

    /**
     * 查询推荐列内容
     * 
     * @param id 推荐列内容主键
     * @return 推荐列内容
     */
    @Override
    public OshRecommendCourses selectOshRecommendCoursesById(String id)
    {
        return oshRecommendCoursesMapper.selectOshRecommendCoursesById(id);
    }

    /**
     * 查询推荐列内容列表
     * 
     * @param oshRecommendCourses 推荐列内容
     * @return 推荐列内容
     */
    @Override
    public List<OshRecommendCourses> selectOshRecommendCoursesList(OshRecommendCourses oshRecommendCourses)
    {
        return oshRecommendCoursesMapper.selectOshRecommendCoursesList(oshRecommendCourses);
    }

    /**
     * 新增推荐列内容
     * 
     * @param oshRecommendCourses 推荐列内容
     * @return 结果
     */
    @Override
    public int insertOshRecommendCourses(OshRecommendCourses oshRecommendCourses)
    {
        oshRecommendCourses.setCreateTime(DateUtils.getNowDate());
        return oshRecommendCoursesMapper.insertOshRecommendCourses(oshRecommendCourses);
    }

    /**
     * 修改推荐列内容
     * 
     * @param oshRecommendCourses 推荐列内容
     * @return 结果
     */
    @Override
    public int updateOshRecommendCourses(OshRecommendCourses oshRecommendCourses)
    {
        oshRecommendCourses.setUpdateTime(DateUtils.getNowDate());
        return oshRecommendCoursesMapper.updateOshRecommendCourses(oshRecommendCourses);
    }

    /**
     * 批量删除推荐列内容
     * 
     * @param ids 需要删除的推荐列内容主键
     * @return 结果
     */
    @Override
    public int deleteOshRecommendCoursesByIds(String[] ids)
    {
        return oshRecommendCoursesMapper.deleteOshRecommendCoursesByIds(ids);
    }

    /**
     * 删除推荐列内容信息
     * 
     * @param id 推荐列内容主键
     * @return 结果
     */
    @Override
    public int deleteOshRecommendCoursesById(String id)
    {
        return oshRecommendCoursesMapper.deleteOshRecommendCoursesById(id);
    }
}
