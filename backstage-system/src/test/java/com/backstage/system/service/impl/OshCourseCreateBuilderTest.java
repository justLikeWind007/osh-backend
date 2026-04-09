package com.backstage.system.service.impl;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.user.User;
import com.backstage.system.request.CourseCreateRequest;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class OshCourseCreateBuilderTest {

    @Test
    public void shouldBuildCourseFromCreateRequestAndFillManagedFields() {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setTitle("  Java 训练营  ");
        request.setCover("https://cdn.example.com/cover.png");
        request.setIntro("  从入门到项目实战  ");
        request.setServiceContent("  社群答疑 + 作业批改  ");
        request.setPrice(new BigDecimal("199.00"));
        request.setTPrice(new BigDecimal("299.00"));
        request.setType("media");
        request.setFreeType(3);
        request.setAfterServiceDays(365);
        request.setExamId(12);
        request.setRemark("  运营创建  ");
        request.setResourceType(3);
        request.setLevel(2);

        User operator = new User();
        operator.setUsername("teacher_hope");

        OshCourse course = OshCourseServiceImpl.buildCourseForCreate(request, operator);

        Assert.assertEquals("Java 训练营", course.getTitle());
        Assert.assertEquals("https://cdn.example.com/cover.png", course.getCover());
        Assert.assertEquals("从入门到项目实战", course.getIntro());
        Assert.assertEquals("社群答疑 + 作业批改", course.getServiceContent());
        Assert.assertEquals(new BigDecimal("199.00"), course.getPrice());
        Assert.assertEquals(new BigDecimal("299.00"), course.gettPrice());
        Assert.assertEquals("media", course.getType());
        Assert.assertEquals(Integer.valueOf(3), course.getFreeType());
        Assert.assertEquals(Integer.valueOf(365), course.getAfterServiceDays());
        Assert.assertEquals(Integer.valueOf(12), course.getExamId());
        Assert.assertEquals("运营创建", course.getRemark());
        Assert.assertEquals(Integer.valueOf(3), course.getResourceType());
        Assert.assertEquals(Integer.valueOf(2), course.getLevel());
        Assert.assertEquals(Integer.valueOf(0), course.getSubCount());
        Assert.assertEquals(Integer.valueOf(0), course.getTotalDuration());
        Assert.assertEquals(Integer.valueOf(0), course.getVideoCount());
        Assert.assertEquals(Integer.valueOf(0), course.getSalesCount());
        Assert.assertEquals(Long.valueOf(0L), course.getViewCount());
        Assert.assertEquals(Integer.valueOf(0), course.getFreeLessonCount());
        Assert.assertEquals(Integer.valueOf(0), course.getLikeCount());
        Assert.assertEquals(Integer.valueOf(0), course.getCommentCount());
        Assert.assertEquals(BigDecimal.ZERO, course.getRatingScore());
        Assert.assertEquals(Integer.valueOf(0), course.getStatus());
        Assert.assertEquals("teacher_hope", course.getCreateBy());
        Assert.assertEquals("teacher_hope", course.getUpdateBy());
    }
}
