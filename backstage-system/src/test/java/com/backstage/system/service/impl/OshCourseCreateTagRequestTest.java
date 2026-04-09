package com.backstage.system.service.impl;

import com.backstage.system.domain.course.vo.OshCourseTagSimpleVo;
import com.backstage.system.request.CourseCreateRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class OshCourseCreateTagRequestTest {

    @Test
    public void shouldTrimTagNamesWhenSettingCourseCreateRequestTags() {
        CourseCreateRequest request = new CourseCreateRequest();
        OshCourseTagSimpleVo first = new OshCourseTagSimpleVo();
        first.setName("  考研  ");
        OshCourseTagSimpleVo second = new OshCourseTagSimpleVo();
        second.setName(" ");

        request.setTags(Arrays.asList(first, second));

        Assert.assertEquals("考研", request.getTags().get(0).getName());
        Assert.assertNull(request.getTags().get(1).getName());
    }
}
