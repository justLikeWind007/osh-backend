package com.backstage.system.service.impl;

import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OshCourseServiceResourceTypeTest {

    @Test
    public void shouldConvertEnglishResourceTypeToChineseWhenQueryingCourseList() {
        CourseSearchLoginVo course = new CourseSearchLoginVo();
        course.setResourceType("FREE");

        List<CourseSearchLoginVo> result = OshCourseServiceImpl.fillResourceTypeDesc(Collections.singletonList(course), null);

        assertEquals("FREE", result.get(0).getResourceType());
        assertEquals("免费", result.get(0).getResourceTypeDesc());
    }

    @Test
    public void shouldReturnPurchasedDescWhenUserBoughtCourse() {
        CourseSearchLoginVo course = new CourseSearchLoginVo();
        course.setResourceType("CASH_ONLY");
        course.setBuyFlag(1);

        List<CourseSearchLoginVo> result = OshCourseServiceImpl.fillResourceTypeDesc(Collections.singletonList(course), 100L);

        assertEquals("CASH_ONLY", result.get(0).getResourceType());
        assertEquals("已购买", result.get(0).getResourceTypeDesc());
    }

    @Test
    public void shouldKeepEnumDescForVipEvenWhenUserBoughtCourse() {
        CourseSearchLoginVo course = new CourseSearchLoginVo();
        course.setResourceType("VIP");
        course.setBuyFlag(1);

        List<CourseSearchLoginVo> result = OshCourseServiceImpl.fillResourceTypeDesc(Collections.singletonList(course), 100L);

        assertEquals("VIP", result.get(0).getResourceType());
        assertEquals("VIP免费", result.get(0).getResourceTypeDesc());
    }
}
