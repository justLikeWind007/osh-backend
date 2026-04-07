package com.backstage.system.service.impl;

import com.backstage.system.constants.CourseConstants;
import com.backstage.system.constants.CourseSectionConstants;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.OshCourseSection;
import com.backstage.system.domain.course.OshCourseMaterial;
import com.backstage.system.domain.course.vo.OshCourseDetailVo;
import com.backstage.system.domain.course.vo.OshCourseSectionVo;
import com.backstage.system.domain.user.User;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.request.CourseCreateRequest;
import com.backstage.system.request.CourseChapterCreateRequest;
import com.backstage.system.request.CourseSearchRequest;
import com.backstage.system.request.CourseTextSectionCreateRequest;
import com.backstage.system.request.CourseVideoSectionCreateRequest;
import com.backstage.system.service.IOshCourseService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 课程信息 Service 业务层处理
 *
 * @author ruoyi
 * @date 2026-01-XX
 */
@Service
public class OshCourseServiceImpl implements IOshCourseService {
    @Autowired
    private OshCourseMapper oshCourseMapper;

    @Override
    public List<OshCourse> pageQuerySearchCourse(CourseSearchRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        return oshCourseMapper.pageQuerySearchCourse(request);
    }

    @Override
    public List<OshCourse> pageQueryUserCollectionCourse(Long userId, CourseSearchRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        return oshCourseMapper.pageQueryUserCollectionCourse(userId, request);
    }

    @Override
    public OshCourseDetailVo getCourseDetail(Long id, Long userId) {
        OshCourseDetailVo oshCourseDetailVo = oshCourseMapper.getCourseDetail(id, userId);
        return oshCourseDetailVo;
    }

    @Override
    public Integer isUserBuyCourseOrFreeCourse(Long courseId, Long userId) {
        return oshCourseMapper.isUserBuyCourseOrFreeCourse(courseId, userId);
    }

    @Override
    public boolean hasUserBoughtCourse(Long courseId, Long userId) {
        return oshCourseMapper.countUserBoughtCourse(courseId, userId) > 0;
    }

    @Override
    public boolean canUserAskQuestion(Long courseId, Long sectionId, Long userId) {
        if (courseId == null) {
            return false;
        }
        if (userId != null && oshCourseMapper.countUserBoughtCourse(courseId, userId) > 0) {
            return true;
        }
        if (oshCourseMapper.countFreeCourse(courseId) > 0) {
            return true;
        }
        return sectionId != null && oshCourseMapper.countFreeSectionInCourse(courseId, sectionId) > 0;
    }

    @Override
    public String getCourseSectionContent(Long sectionId, Long userId) {
        return oshCourseMapper.getCourseSectionContent(sectionId);
    }

    @Override
    public List<OshCourseMaterial> getCourseMaterials(Long courseId) {
        List<OshCourseMaterial> courseMaterials = oshCourseMapper.getCourseMaterials(courseId);
        return courseMaterials;
    }

    @Override
    public List<OshCourseSectionVo> getCourseSectionOutline(Long courseId) {
        return buildSectionTree(oshCourseMapper.selectCourseSectionList(courseId));
    }

    @Override
    public String getTextCourseSectionContent(Long sectionId) {
        return oshCourseMapper.selectTextCourseSectionContent(sectionId);
    }

    /**
     * 查询课程详情
     *
     * @param id 课程 ID
     * @return 课程信息
     */
    @Override
    public OshCourse selectCourseById(Long id) {
        return oshCourseMapper.selectCourseById(id);
    }

    /**
     * 查询课程列表
     *
     * @return 课程集合
     */
    @Override
    public List<OshCourse> selectCourseList() {
        return oshCourseMapper.selectCourseList();
    }

    /**
     * 新增课程
     *
     * @param course 课程信息
     * @return 结果
     */
    @Override
    public int insertCourse(OshCourse course) {
        return oshCourseMapper.insertCourse(course);
    }

    @Override
    public Long createCourse(CourseCreateRequest request, User operator) {
        OshCourse course = buildCourseForCreate(request, operator);
        int rows = oshCourseMapper.insertCourse(course);
        return rows > 0 ? course.getId() : null;
    }

    @Override
    public Long createCourseChapter(CourseChapterCreateRequest request, User operator) {
        ensureCourseExists(request.getCourseId());
        OshCourseSection section = buildChapterSectionForCreate(request, operator);
        return insertCourseSection(section);
    }

    @Override
    public Long createCourseVideoSection(CourseVideoSectionCreateRequest request, User operator) {
        ensureCourseExists(request.getCourseId());
        ensureParentChapter(request.getCourseId(), request.getParentId());
        OshCourseSection section = buildVideoSectionForCreate(request, operator);
        return insertCourseSection(section);
    }

    @Override
    public Long createCourseTextSection(CourseTextSectionCreateRequest request, User operator) {
        ensureCourseExists(request.getCourseId());
        ensureParentChapter(request.getCourseId(), request.getParentId());
        OshCourseSection section = buildTextSectionForCreate(request, operator);
        return insertCourseSection(section);
    }

    /**
     * 修改课程
     *
     * @param course 课程信息
     * @return 结果
     */
    @Override
    public int updateCourse(OshCourse course) {
        return oshCourseMapper.updateCourse(course);
    }

    /**
     * 批量删除课程
     *
     * @param ids 需要删除的课程 ID
     * @return 结果
     */
    @Override
    public int deleteCourseByIds(Long[] ids) {
        return oshCourseMapper.deleteCourseByIds(ids);
    }

    /**
     * 删除课程
     *
     * @param id 课程 ID
     * @return 结果
     */
    @Override
    public int deleteCourseById(Long id) {
        return oshCourseMapper.deleteCourseById(id);
    }

    static List<OshCourseSectionVo> buildSectionTree(List<OshCourseSectionVo> sectionList) {
        if (sectionList == null || sectionList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, OshCourseSectionVo> nodeMap = new HashMap<>();
        for (OshCourseSectionVo section : sectionList) {
            section.setChildren(new ArrayList<>());
            nodeMap.put(section.getId(), section);
        }

        List<OshCourseSectionVo> roots = new ArrayList<>();
        for (OshCourseSectionVo section : sectionList) {
            Long parentId = section.getParentId();
            if (parentId == null || parentId == 0L) {
                roots.add(section);
                continue;
            }
            OshCourseSectionVo parent = nodeMap.get(parentId);
            if (parent == null) {
                roots.add(section);
                continue;
            }
            parent.getChildren().add(section);
        }

        sortSections(roots);
        return roots;
    }

    private static void sortSections(List<OshCourseSectionVo> sections) {
        sections.sort(Comparator
                .comparing(OshCourseSectionVo::getSort, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(OshCourseSectionVo::getId, Comparator.nullsLast(Long::compareTo)));
        for (OshCourseSectionVo section : sections) {
            if (section.getChildren() != null && !section.getChildren().isEmpty()) {
                sortSections(section.getChildren());
            }
        }
    }

    static OshCourse buildCourseForCreate(CourseCreateRequest request, User operator) {
        OshCourse course = new OshCourse();
        course.setTitle(StringUtils.trimToNull(request.getTitle()));
        course.setCover(StringUtils.trimToNull(request.getCover()));
        course.setIntro(StringUtils.trimToNull(request.getIntro()));
        course.setServiceContent(StringUtils.trimToNull(request.getServiceContent()));
        course.setPrice(request.getPrice());
        course.setTPrice(request.getTPrice());
        course.setType(StringUtils.trimToNull(request.getType()));
        course.setFreeType(defaultInteger(request.getFreeType()));
        course.setAfterServiceDays(defaultInteger(request.getAfterServiceDays()));
        course.setExamId(request.getExamId());
        course.setRemark(StringUtils.trimToNull(request.getRemark()));

        course.setSubCount(CourseConstants.DEFAULT_COUNT);
        course.setTotalDuration(CourseConstants.DEFAULT_COUNT);
        course.setVideoCount(CourseConstants.DEFAULT_COUNT);
        course.setSalesCount(CourseConstants.DEFAULT_COUNT);
        course.setViewCount(CourseConstants.DEFAULT_LONG_COUNT);
        course.setFreeLessonCount(CourseConstants.DEFAULT_COUNT);
        course.setLikeCount(CourseConstants.DEFAULT_COUNT);
        course.setCommentCount(CourseConstants.DEFAULT_COUNT);
        course.setRatingScore(CourseConstants.DEFAULT_RATING_SCORE);
        course.setStatus(CourseConstants.STATUS_DRAFT);

        String operatorName = operator == null ? null : StringUtils.trimToNull(operator.getUsername());
        course.setCreateBy(operatorName);
        course.setUpdateBy(operatorName);
        return course;
    }

    private static Integer defaultInteger(Integer value) {
        return value == null ? CourseConstants.DEFAULT_COUNT : value;
    }

    static OshCourseSection buildChapterSectionForCreate(CourseChapterCreateRequest request, User operator) {
        OshCourseSection section = buildBaseSection(request.getCourseId(), CourseSectionConstants.ROOT_PARENT_ID,
                request.getTitle(), request.getSort(), operator);
        section.setFreeFlag(CourseSectionConstants.CHAPTER_FREE_FLAG);
        return section;
    }

    static OshCourseSection buildVideoSectionForCreate(CourseVideoSectionCreateRequest request, User operator) {
        OshCourseSection section = buildBaseSection(request.getCourseId(), request.getParentId(),
                request.getTitle(), request.getSort(), operator);
        section.setFreeFlag(defaultFreeFlag(request.getFreeFlag()));
        section.setDuration(request.getDuration());
        section.setMediaUrl(StringUtils.trimToNull(request.getMediaUrl()));
        section.setCover(StringUtils.trimToNull(request.getCover()));
        section.setVideoDesc(StringUtils.trimToNull(request.getVideoDesc()));
        section.setTextContent(StringUtils.trimToNull(request.getTextContent()));
        section.setFileSize(request.getFileSize());
        section.setType(CourseSectionConstants.TYPE_VIDEO);
        return section;
    }

    static OshCourseSection buildTextSectionForCreate(CourseTextSectionCreateRequest request, User operator) {
        OshCourseSection section = buildBaseSection(request.getCourseId(), request.getParentId(),
                request.getTitle(), request.getSort(), operator);
        section.setFreeFlag(defaultFreeFlag(request.getFreeFlag()));
        section.setCover(StringUtils.trimToNull(request.getCover()));
        section.setTextContent(StringUtils.trimToNull(request.getTextContent()));
        section.setType(CourseSectionConstants.TYPE_TEXT);
        return section;
    }

    private Long insertCourseSection(OshCourseSection section) {
        int rows = oshCourseMapper.insertCourseSection(section);
        return rows > 0 ? section.getId() : null;
    }

    private void ensureCourseExists(Long courseId) {
        if (oshCourseMapper.selectCourseById(courseId) == null) {
            throw new IllegalArgumentException("课程不存在");
        }
    }

    private void ensureParentChapter(Long courseId, Long parentId) {
        OshCourseSection parent = oshCourseMapper.selectCourseSectionById(parentId);
        if (parent == null || parent.getDeleteFlag() == null || parent.getDeleteFlag() != CourseSectionConstants.DELETE_FLAG_NORMAL) {
            throw new IllegalArgumentException("父章节不存在");
        }
        if (!courseId.equals(parent.getCourseId())) {
            throw new IllegalArgumentException("父章节不属于当前课程");
        }
        if (parent.getParentId() == null || !CourseSectionConstants.ROOT_PARENT_ID.equals(parent.getParentId())) {
            throw new IllegalArgumentException("父章节必须为一级章节");
        }
    }

    private static OshCourseSection buildBaseSection(Long courseId, Long parentId, String title, Integer sort, User operator) {
        OshCourseSection section = new OshCourseSection();
        Date now = new Date();
        section.setCourseId(courseId);
        section.setParentId(parentId);
        section.setTitle(StringUtils.trimToNull(title));
        section.setSort(sort);
        section.setStatus(CourseSectionConstants.STATUS_NORMAL);
        section.setDeleteFlag(CourseSectionConstants.DELETE_FLAG_NORMAL);
        String operatorName = operator == null ? null : StringUtils.trimToNull(operator.getUsername());
        section.setCreateBy(operatorName);
        section.setCreateTime(now);
        section.setUpdateBy(operatorName);
        section.setUpdateTime(now);
        return section;
    }

    private static Integer defaultFreeFlag(Integer freeFlag) {
        return freeFlag == null ? CourseSectionConstants.DEFAULT_FREE_FLAG : freeFlag;
    }
}
