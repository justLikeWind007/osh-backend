package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseSection;
import com.backstage.system.domain.course.OshCourseMaterial;
import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import com.backstage.system.domain.course.vo.OshCourseDetailVo;
import com.backstage.system.domain.course.vo.OshCourseSectionVo;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.request.CourseSearchRequest;
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


    List<CourseSearchLoginVo> pageQuerySearchCourse(CourseSearchRequest request);

    List<CourseSearchLoginVo> pageQueryLoginSearchCourse(@Param("userId") Long userId, @Param("request") CourseSearchRequest request);

    List<OshCourse> pageQueryUserCollectionCourse(@Param("userId") Long userId, @Param("request") CourseSearchRequest request);
    /**
     * 查询课程信息
     *
     * @param id 课程 ID
     * @return 课程信息
     */
    OshCourse selectCourseById(Long id);

    /**
     * 查询课程列表
     *
     * @return 课程列表
     */
    List<OshCourse> selectCourseList();

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

    OshCourseDetailVo getCourseDetail(@Param("id") Long id, @Param("userId") Long userId);

    Integer isUserBuyCourseOrFreeCourse(Long courseId, Long userId);

    Integer countUserBoughtCourse(@Param("courseId") Long courseId, @Param("userId") Long userId);

    Integer countFreeCourse(@Param("courseId") Long courseId);

    Integer countFreeSectionInCourse(@Param("courseId") Long courseId, @Param("sectionId") Long sectionId);

    int increaseQuestionCount(@Param("courseId") Long courseId);

    int increaseCollectionCount(@Param("courseId") Long courseId);

    int decreaseCollectionCount(@Param("courseId") Long courseId);

    List<OshCourseSectionVo> selectCourseSectionList(@Param("courseId") Long courseId);

    Integer countCourseSectionInCourse(@Param("courseId") Long courseId, @Param("sectionId") Long sectionId);

    OshCourseSection selectCourseSectionById(@Param("id") Long id);

    int insertCourseSection(OshCourseSection section);

    String getCourseSectionContent(@Param("sectionId") Long sectionId);

    List<OshCourseMaterial> getCourseMaterials(Long courseId);

    /**
     * 更新课程章节数统计
     *
     * @param params 包含courseId等参数的Map
     * @return 更新结果
     */
    int updateCourseSubCount(Map<String, Object> params);
}
