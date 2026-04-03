package com.backstage.system.service.impl;

import com.backstage.system.domain.course.OshCourseSection;
import com.backstage.system.domain.user.User;
import com.backstage.system.request.CourseChapterCreateRequest;
import com.backstage.system.request.CourseTextSectionCreateRequest;
import com.backstage.system.request.CourseVideoSectionCreateRequest;
import org.junit.Assert;
import org.junit.Test;

public class OshCourseSectionCreateBuilderTest {

    @Test
    public void shouldBuildChapterSectionWithManagedDefaults() {
        CourseChapterCreateRequest request = new CourseChapterCreateRequest();
        request.setCourseId(100L);
        request.setTitle("  第一章  ");
        request.setSort(1);

        User operator = new User();
        operator.setUsername("teacher_hope");

        OshCourseSection section = OshCourseServiceImpl.buildChapterSectionForCreate(request, operator);

        Assert.assertEquals(Long.valueOf(100L), section.getCourseId());
        Assert.assertEquals(Long.valueOf(0L), section.getParentId());
        Assert.assertEquals("第一章", section.getTitle());
        Assert.assertEquals(Integer.valueOf(1), section.getSort());
        Assert.assertEquals(Integer.valueOf(1), section.getFreeFlag());
        Assert.assertEquals(Integer.valueOf(1), section.getStatus());
        Assert.assertEquals(Integer.valueOf(0), section.getDeleteFlag());
        Assert.assertEquals("teacher_hope", section.getCreateBy());
        Assert.assertEquals("teacher_hope", section.getUpdateBy());
    }

    @Test
    public void shouldBuildVideoSectionWithVideoFieldsAndType() {
        CourseVideoSectionCreateRequest request = new CourseVideoSectionCreateRequest();
        request.setCourseId(100L);
        request.setParentId(10L);
        request.setTitle("  第一节  ");
        request.setSort(2);
        request.setFreeFlag(0);
        request.setDuration(600);
        request.setMediaUrl("  https://oss.example.com/video.mp4  ");
        request.setCover("  https://oss.example.com/cover.png ");
        request.setVideoDesc("  1080P 视频  ");
        request.setTextContent("  视频补充说明  ");
        request.setFileSize(123456L);

        User operator = new User();
        operator.setUsername("teacher_hope");

        OshCourseSection section = OshCourseServiceImpl.buildVideoSectionForCreate(request, operator);

        Assert.assertEquals(Long.valueOf(100L), section.getCourseId());
        Assert.assertEquals(Long.valueOf(10L), section.getParentId());
        Assert.assertEquals("第一节", section.getTitle());
        Assert.assertEquals(Integer.valueOf(2), section.getSort());
        Assert.assertEquals(Integer.valueOf(0), section.getFreeFlag());
        Assert.assertEquals(Integer.valueOf(600), section.getDuration());
        Assert.assertEquals("https://oss.example.com/video.mp4", section.getMediaUrl());
        Assert.assertEquals("https://oss.example.com/cover.png", section.getCover());
        Assert.assertEquals("1080P 视频", section.getVideoDesc());
        Assert.assertEquals("视频补充说明", section.getTextContent());
        Assert.assertEquals(Long.valueOf(123456L), section.getFileSize());
        Assert.assertEquals("video", section.getType());
        Assert.assertEquals(Integer.valueOf(1), section.getStatus());
        Assert.assertEquals(Integer.valueOf(0), section.getDeleteFlag());
    }

    @Test
    public void shouldBuildTextSectionWithTextFieldsAndType() {
        CourseTextSectionCreateRequest request = new CourseTextSectionCreateRequest();
        request.setCourseId(100L);
        request.setParentId(10L);
        request.setTitle("  课前须知  ");
        request.setSort(3);
        request.setCover("  https://oss.example.com/text-cover.png ");
        request.setTextContent("  图文内容  ");

        User operator = new User();
        operator.setUsername("teacher_hope");

        OshCourseSection section = OshCourseServiceImpl.buildTextSectionForCreate(request, operator);

        Assert.assertEquals(Long.valueOf(100L), section.getCourseId());
        Assert.assertEquals(Long.valueOf(10L), section.getParentId());
        Assert.assertEquals("课前须知", section.getTitle());
        Assert.assertEquals(Integer.valueOf(3), section.getSort());
        Assert.assertEquals(Integer.valueOf(0), section.getFreeFlag());
        Assert.assertEquals("https://oss.example.com/text-cover.png", section.getCover());
        Assert.assertEquals("图文内容", section.getTextContent());
        Assert.assertEquals("text", section.getType());
        Assert.assertEquals(Integer.valueOf(1), section.getStatus());
        Assert.assertEquals(Integer.valueOf(0), section.getDeleteFlag());
    }
}
