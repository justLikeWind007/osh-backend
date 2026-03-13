package com.backstage.system.service.common;

import java.util.List;
import com.backstage.system.domain.common.OshRecommendCourses;

/**
 * 推荐列内容Service接口
 * 
 * @author ruoyi
 * @date 2026-03-10
 */
public interface IOshRecommendCoursesService 
{
    /**
     * 查询推荐列内容
     * 
     * @param id 推荐列内容主键
     * @return 推荐列内容
     */
    public OshRecommendCourses selectOshRecommendCoursesById(String id);

    /**
     * 查询推荐列内容列表
     * 
     * @param oshRecommendCourses 推荐列内容
     * @return 推荐列内容集合
     */
    public List<OshRecommendCourses> selectOshRecommendCoursesList(OshRecommendCourses oshRecommendCourses);

    /**
     * 新增推荐列内容
     * 
     * @param oshRecommendCourses 推荐列内容
     * @return 结果
     */
    public int insertOshRecommendCourses(OshRecommendCourses oshRecommendCourses);

    /**
     * 修改推荐列内容
     * 
     * @param oshRecommendCourses 推荐列内容
     * @return 结果
     */
    public int updateOshRecommendCourses(OshRecommendCourses oshRecommendCourses);

    /**
     * 批量删除推荐列内容
     * 
     * @param ids 需要删除的推荐列内容主键集合
     * @return 结果
     */
    public int deleteOshRecommendCoursesByIds(String[] ids);

    /**
     * 删除推荐列内容信息
     * 
     * @param id 推荐列内容主键
     * @return 结果
     */
    public int deleteOshRecommendCoursesById(String id);
}
