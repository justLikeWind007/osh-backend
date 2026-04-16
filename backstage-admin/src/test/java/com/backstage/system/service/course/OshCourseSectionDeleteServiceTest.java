package com.backstage.system.service.course;

import com.backstage.system.constants.CourseSectionConstants;
import com.backstage.system.domain.course.OshCourseSection;
import com.backstage.system.domain.user.User;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseMaterialMapper;
import com.backstage.system.mapper.course.OshCourseTagMapper;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.impl.OshCourseServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OshCourseSectionDeleteServiceTest {

    private OshCourseMapper oshCourseMapper;
    private OshCourseServiceImpl oshCourseService;
    private User operator;

    @Before
    public void setUp() {
        oshCourseMapper = mock(OshCourseMapper.class);

        oshCourseService = new OshCourseServiceImpl();
        ReflectionTestUtils.setField(oshCourseService, "oshCourseMapper", oshCourseMapper);
        ReflectionTestUtils.setField(oshCourseService, "oshCourseTagMapper", mock(OshCourseTagMapper.class));
        ReflectionTestUtils.setField(oshCourseService, "oshCourseMaterialMapper", mock(OshCourseMaterialMapper.class));
        ReflectionTestUtils.setField(oshCourseService, "courseManageService", mock(ICourseManageService.class));
        ReflectionTestUtils.setField(oshCourseService, "ossService", mock(OssService.class));
        ReflectionTestUtils.setField(oshCourseService, "courseIndexKafkaProducer", mock(CourseIndexKafkaProducer.class));
        ReflectionTestUtils.setField(oshCourseService, "courseIndexMessageMapper", Mappers.getMapper(CourseIndexMessageMapper.class));

        operator = new User();
        operator.setUsername("tester");
    }

    @Test
    public void shouldRejectDeleteWhenSectionDoesNotBelongToCourse() {
        OshCourseSection section = new OshCourseSection();
        section.setId(34L);
        section.setCourseId(99L);
        section.setParentId(CourseSectionConstants.ROOT_PARENT_ID);
        section.setDeleteFlag(CourseSectionConstants.DELETE_FLAG_NORMAL);
        when(oshCourseMapper.selectCourseSectionById(34L)).thenReturn(section);

        boolean deleted = oshCourseService.safeDeleteSection(12L, 34L, operator);

        assertFalse(deleted);
        verify(oshCourseMapper, never()).deleteCourseSectionsByParentId(anyLong(), eq("tester"));
        verify(oshCourseMapper, never()).deleteCourseSectionById(anyLong(), eq("tester"));
    }

    @Test
    public void shouldDeleteChapterAndChildSectionsWhenCourseMatches() {
        OshCourseSection section = new OshCourseSection();
        section.setId(34L);
        section.setCourseId(12L);
        section.setParentId(CourseSectionConstants.ROOT_PARENT_ID);
        section.setDeleteFlag(CourseSectionConstants.DELETE_FLAG_NORMAL);
        when(oshCourseMapper.selectCourseSectionById(34L)).thenReturn(section);
        when(oshCourseMapper.deleteCourseSectionsByParentId(34L, "tester")).thenReturn(2);
        when(oshCourseMapper.deleteCourseSectionById(34L, "tester")).thenReturn(1);

        boolean deleted = oshCourseService.safeDeleteSection(12L, 34L, operator);

        assertTrue(deleted);
        verify(oshCourseMapper).deleteCourseSectionsByParentId(34L, "tester");
        verify(oshCourseMapper).deleteCourseSectionById(34L, "tester");
    }
}
