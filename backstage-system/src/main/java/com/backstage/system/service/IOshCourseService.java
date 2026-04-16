package com.backstage.system.service;

import com.backstage.system.domain.course.OshCourseMaterial;
import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import com.backstage.system.domain.course.vo.OshCourseDetailVo;
import com.backstage.system.domain.course.vo.OshCourseSectionVo;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.user.User;
import com.backstage.system.request.CourseCreateRequest;
import com.backstage.system.request.CourseChapterCreateRequest;
import com.backstage.system.request.CourseSearchRequest;
import com.backstage.system.request.CourseTextSectionCreateRequest;
import com.backstage.system.request.CourseUpdateRequest;
import com.backstage.system.request.CourseVideoSectionCreateRequest;

import java.util.List;

/**
 * 课程信息 Service 接口
 *
 * @author ruoyi
 * @date 2026-01-XX
 */
public interface IOshCourseService {
    List<CourseSearchLoginVo> pageQuerySearchCourse(CourseSearchRequest request);

    List<CourseSearchLoginVo> pageQueryLoginSearchCourse(Long userId, CourseSearchRequest request);

    List<OshCourse> pageQueryUserCollectionCourse(Long userId, CourseSearchRequest request);

    /**
     * 查询课程详情
     *
     * @param id 课程 ID
     * @return 课程信息
     */
    OshCourse selectCourseById(Long id);

    /**
     * 查询课程列表
     *
     * @return 课程集合
     */
    List<OshCourse> selectCourseList();

    /**
     * 新增课程
     *
     * @param course 课程信息
     * @return 结果
     */
    int insertCourse(OshCourse course);

    Long createCourse(CourseCreateRequest request, User operator);

    Long updateCourse(CourseUpdateRequest request, User operator);

    Long createCourseChapter(CourseChapterCreateRequest request, User operator);

    Long createCourseTextSection(CourseTextSectionCreateRequest request, User operator);

    Long createCourseVideoSection(CourseVideoSectionCreateRequest request, User operator);

    /**
     * 修改课程
     *
     * @param course 课程信息
     * @return 结果
     */
    int updateCourse(OshCourse course);

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

    OshCourseDetailVo getCourseDetail(Long id, Long userId);

    List<OshCourseSectionVo> getCourseSectionOutline(Long courseId);

    Integer isUserBuyCourseOrFreeCourse(Long courseId, Long userId);

    boolean hasUserBoughtCourse(Long courseId, Long userId);

    boolean canUserAskQuestion(Long courseId, Long sectionId, Long userId);

    String getCourseSectionContent(Long sectionId, Long userId);

    List<OshCourseMaterial> getCourseMaterials(Long courseId);

    boolean safeDeleteSection(Long courseId, Long sectionId, User currentUser);
}
