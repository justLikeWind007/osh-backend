package com.backstage.system.service.impl;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.vo.OshCourseDetailVo;
import com.backstage.system.domain.course.vo.OshCourseSectionVo;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.request.CourseSearchRequest;
import com.backstage.system.service.IOshCouresService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private OshCourseMapper oshCourseMapper;

    @Override
    public List<OshCourse> pageQuerySearchCourse(CourseSearchRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        return oshCourseMapper.pageQuerySearchCourse(request);
    }

    @Override
    public OshCourseDetailVo getCourseDetail(Long id) {
        OshCourseDetailVo oshCourseDetailVo = oshCourseMapper.getCourseDetail(id);
        return oshCourseDetailVo;
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
    public OshCourse selectCourseById(Long id)
    {
        return oshCourseMapper.selectCourseById(id);
    }

    /**
     * 查询课程列表
     *
     * @return 课程集合
     */
    @Override
    public List<OshCourse> selectCourseList()
    {
        return oshCourseMapper.selectCourseList();
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
        return oshCourseMapper.insertCourse(course);
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
        return oshCourseMapper.updateCourse(course);
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
        return oshCourseMapper.deleteCourseByIds(ids);
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
}
