package com.backstage.system.enums;

import org.junit.Assert;
import org.junit.Test;

public class CourseResourceEnumTest {

    @Test
    public void shouldResolveCourseResourceByCode() {
        Assert.assertEquals(Integer.valueOf(1), CourseResourceEnum.FREE.getCode());
        Assert.assertEquals("免费", CourseResourceEnum.FREE.getDesc());
        Assert.assertEquals(CourseResourceEnum.FREE, CourseResourceEnum.fromCode(1));
        Assert.assertEquals(CourseResourceEnum.CASH_PAY, CourseResourceEnum.fromCode(2));
        Assert.assertEquals(CourseResourceEnum.POINTS_OR_CASH_PAY, CourseResourceEnum.fromCode(3));
        Assert.assertEquals(CourseResourceEnum.VIP_ONLY, CourseResourceEnum.fromCode(4));
        Assert.assertEquals(CourseResourceEnum.SMALL_CLASS_ONLY, CourseResourceEnum.fromCode(5));
        Assert.assertEquals(CourseResourceEnum.INTERNAL_MEMBER_ONLY, CourseResourceEnum.fromCode(6));
        Assert.assertNull(CourseResourceEnum.fromCode(null));
        Assert.assertNull(CourseResourceEnum.fromCode(99));
    }
}
