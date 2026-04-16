package com.backstage.system.service.impl;

import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class OshCourseSearchLoginVoEnrichTest {

    @Test
    public void shouldFillResourceTypeDescForSearchVoList() {
        CourseSearchLoginVo first = new CourseSearchLoginVo();
        first.setResourceType(1);
        CourseSearchLoginVo second = new CourseSearchLoginVo();
        second.setResourceType(4);
        CourseSearchLoginVo third = new CourseSearchLoginVo();
        third.setResourceType(99);

        List<CourseSearchLoginVo> result = OshCourseServiceImpl.fillResourceTypeDesc(Arrays.asList(first, second, third));

        Assert.assertEquals("免费", result.get(0).getResourceTypeDesc());
        Assert.assertEquals("vip专属", result.get(1).getResourceTypeDesc());
        Assert.assertNull(result.get(2).getResourceTypeDesc());
    }
}
