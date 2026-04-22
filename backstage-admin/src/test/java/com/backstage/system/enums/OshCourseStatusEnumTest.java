package com.backstage.system.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OshCourseStatusEnumTest {

    @Test
    public void shouldExposeExpectedCourseStatusCodesAndDescriptions() {
        assertEquals(Integer.valueOf(0), OshCourseStatusEnum.DRAFT.getCode());
        assertEquals("草稿", OshCourseStatusEnum.DRAFT.getDesc());

        assertEquals(Integer.valueOf(1), OshCourseStatusEnum.PENDING_AUDIT.getCode());
        assertEquals("待审核", OshCourseStatusEnum.PENDING_AUDIT.getDesc());

        assertEquals(Integer.valueOf(2), OshCourseStatusEnum.PUBLISHED.getCode());
        assertEquals("已发布", OshCourseStatusEnum.PUBLISHED.getDesc());

        assertEquals(Integer.valueOf(3), OshCourseStatusEnum.OFF_SHELF.getCode());
        assertEquals("已下架", OshCourseStatusEnum.OFF_SHELF.getDesc());
    }
}
