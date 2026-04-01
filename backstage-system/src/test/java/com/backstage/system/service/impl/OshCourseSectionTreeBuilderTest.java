package com.backstage.system.service.impl;

import com.backstage.system.domain.course.vo.OshCourseSectionVo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class OshCourseSectionTreeBuilderTest {

    @Test
    public void shouldBuildTreeAndSortBySortWithinSameLevel() {
        OshCourseSectionVo chapterB = createNode(2L, 100L, 0L, "第二章", 20);
        OshCourseSectionVo chapterA = createNode(1L, 100L, 0L, "第一章", 10);
        OshCourseSectionVo sectionB = createNode(4L, 100L, 1L, "第一节", 20);
        OshCourseSectionVo sectionA = createNode(3L, 100L, 1L, "前言", 10);

        List<OshCourseSectionVo> tree = OshCouresServiceImpl.buildSectionTree(Arrays.asList(
                chapterB, sectionB, chapterA, sectionA));
        System.out.println(chapterA);
        Assert.assertEquals(2, tree.size());
        Assert.assertEquals(Long.valueOf(1L), tree.get(0).getId());
        Assert.assertEquals(Long.valueOf(2L), tree.get(1).getId());
        Assert.assertEquals(2, tree.get(0).getChildren().size());
        Assert.assertEquals(Long.valueOf(3L), tree.get(0).getChildren().get(0).getId());
        Assert.assertEquals(Long.valueOf(4L), tree.get(0).getChildren().get(1).getId());
    }

    private OshCourseSectionVo createNode(Long id, Long courseId, Long parentId, String title, Integer sort) {
        OshCourseSectionVo vo = new OshCourseSectionVo();
        vo.setId(id);
        vo.setCourseId(courseId);
        vo.setParentId(parentId);
        vo.setTitle(title);
        vo.setSort(sort);
        return vo;
    }
}
