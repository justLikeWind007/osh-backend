package com.backstage.system.mapper.common;

import java.util.List;
import com.backstage.system.domain.common.OshRecommendCourses;

/**
 * 推荐列内容Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-10
 */
public interface OshRecommendCoursesMapper 
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
     * 删除推荐列内容
     * 
     * @param id 推荐列内容主键
     * @return 结果
     */
    public int deleteOshRecommendCoursesById(String id);

    /**
     * 批量删除推荐列内容
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOshRecommendCoursesByIds(String[] ids);
}
