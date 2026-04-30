package com.backstage.system.service;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseMaterialMapper;
import com.backstage.system.mapper.course.OshCourseTagMapper;
import com.backstage.system.request.CourseUpdateRequest;
import com.backstage.system.request.CourseCreateRequest;
import com.backstage.system.service.course.CourseIndexDeleteMessage;
import com.backstage.system.service.course.CourseIndexMessageMapper;
import com.backstage.system.service.course.CourseIndexUpsertMessage;
import com.backstage.system.service.impl.OshCourseServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OshCourseServiceOutboxTest {

    @InjectMocks
    private OshCourseServiceImpl courseService;

    @Mock
    private OshCourseMapper oshCourseMapper;

    @Mock
    private OshCourseMaterialMapper oshCourseMaterialMapper;

    @Mock
    private OshCourseTagMapper oshCourseTagMapper;

    @Mock
    private CourseIndexMessageMapper courseIndexMessageMapper;

    @Mock
    private OutboxEventService outboxEventService;

    @Test
    public void shouldWriteCourseIndexCreateOutboxEventInsteadOfSendingKafkaWhenCreatingCourse() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setTitle("高可用课程");
        request.setCover("course/cover.png");
        request.setIntro("课程介绍");
        request.setPrice(new BigDecimal("99.00"));
        request.setTPrice(new BigDecimal("199.00"));
        request.setType("media");

        OshUser operator = new OshUser();
        operator.setId(1L);
        operator.setUsername("admin");

        when(oshCourseMapper.insertCourse(any(OshCourse.class))).thenAnswer(invocation -> {
            OshCourse course = invocation.getArgument(0);
            course.setId(10001L);
            return 1;
        });
        when(oshCourseMapper.selectCourseById(10001L)).thenAnswer(invocation -> {
            return buildPersistedCourse(10001L);
        });
        when(courseIndexMessageMapper.toMessage(any(OshCourse.class), eq("admin"))).thenReturn(new CourseIndexUpsertMessage());

        Long courseId = courseService.createCourse(request, operator);

        assertEquals(Long.valueOf(10001L), courseId);
        verify(outboxEventService).saveCourseIndexCreateEvent(eq(10001L), any(CourseIndexUpsertMessage.class), eq(operator));
    }

    @Test
    public void shouldWriteCourseIndexUpdateOutboxEventInsteadOfSendingKafkaWhenUpdatingCourse() {
        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setId(10001L);
        request.setTitle("更新后的课程");
        request.setCover("course/cover-updated.png");
        request.setIntro("更新后的课程介绍");
        request.setPrice(new BigDecimal("88.00"));
        request.setTPrice(new BigDecimal("188.00"));
        request.setType("media");
        request.setTags(Arrays.asList("Java", "Kafka"));

        OshUser operator = new OshUser();
        operator.setId(1L);
        operator.setUsername("admin");

        when(oshCourseMapper.selectCourseById(10001L)).thenReturn(buildPersistedCourse(10001L));
        when(oshCourseMapper.updateCourse(any(OshCourse.class))).thenReturn(1);
        when(courseIndexMessageMapper.toMessage(any(OshCourse.class), eq("admin"))).thenReturn(new CourseIndexUpsertMessage());

        Long courseId = courseService.updateCourse(request, operator);

        assertEquals(Long.valueOf(10001L), courseId);
        verify(outboxEventService).saveCourseIndexUpdateEvent(eq(10001L), any(CourseIndexUpsertMessage.class), eq(operator));
    }

    @Test
    public void shouldWriteCourseIndexDeleteOutboxEventWhenDeletingCourses() {
        OshUser operator = new OshUser();
        operator.setId(1L);
        operator.setUsername("admin");
        ArgumentCaptor<CourseIndexDeleteMessage> messageCaptor = ArgumentCaptor.forClass(CourseIndexDeleteMessage.class);

        when(oshCourseMapper.selectCourseById(10001L)).thenReturn(buildPersistedCourse(10001L));

        courseService.deleteCoursesByIds(Arrays.asList(10001L), operator);

        verify(outboxEventService).saveCourseIndexDeleteEvent(eq(10001L), messageCaptor.capture(), eq(operator));
        assertEquals(Long.valueOf(10001L), messageCaptor.getValue().getId());
    }

    private OshCourse buildPersistedCourse(Long id) {
        OshCourse course = new OshCourse();
        course.setId(id);
        course.setTitle("高可用课程");
        course.setCover("course/cover.png");
        course.setIntro("课程介绍");
        course.setPrice(new BigDecimal("99.00"));
        course.setTPrice(new BigDecimal("199.00"));
        course.setType("media");
        course.setCreateBy("admin");
        course.setUpdateBy("admin");
        return course;
    }
}
