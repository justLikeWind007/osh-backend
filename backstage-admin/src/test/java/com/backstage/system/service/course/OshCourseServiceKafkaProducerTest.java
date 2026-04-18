package com.backstage.system.service.course;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseMaterialMapper;
import com.backstage.system.mapper.course.OshCourseTagMapper;
import com.backstage.system.request.CourseCreateRequest;
import com.backstage.system.request.CourseUpdateRequest;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.impl.OshCourseServiceImpl;
import org.mapstruct.factory.Mappers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OshCourseServiceKafkaProducerTest {

    private OshCourseMapper oshCourseMapper;
    private OshCourseTagMapper oshCourseTagMapper;
    private CourseIndexKafkaProducer courseIndexKafkaProducer;
    private OshCourseServiceImpl oshCourseService;

    @Before
    public void setUp() {
        oshCourseMapper = mock(OshCourseMapper.class);
        oshCourseTagMapper = mock(OshCourseTagMapper.class);
        courseIndexKafkaProducer = mock(CourseIndexKafkaProducer.class);

        oshCourseService = new OshCourseServiceImpl();
        ReflectionTestUtils.setField(oshCourseService, "oshCourseMapper", oshCourseMapper);
        ReflectionTestUtils.setField(oshCourseService, "oshCourseTagMapper", oshCourseTagMapper);
        ReflectionTestUtils.setField(oshCourseService, "oshCourseMaterialMapper", mock(OshCourseMaterialMapper.class));
        ReflectionTestUtils.setField(oshCourseService, "courseManageService", mock(ICourseManageService.class));
        ReflectionTestUtils.setField(oshCourseService, "ossService", mock(OssService.class));
        ReflectionTestUtils.setField(oshCourseService, "courseIndexKafkaProducer", courseIndexKafkaProducer);
        ReflectionTestUtils.setField(oshCourseService, "courseIndexMessageMapper", Mappers.getMapper(CourseIndexMessageMapper.class));
    }

    @Test
    public void shouldSendCourseIndexCreateMessageAfterCreateCourse() {
        doAnswer(invocation -> {
            OshCourse course = invocation.getArgument(0);
            course.setId(123L);
            return 1;
        }).when(oshCourseMapper).insertCourse(any(OshCourse.class));
        doAnswer(invocation -> {
            com.backstage.system.domain.course.OshCourseTag courseTag = invocation.getArgument(0);
            courseTag.setId(88L);
            return 1;
        }).when(oshCourseTagMapper).insertCourseTag(any(com.backstage.system.domain.course.OshCourseTag.class));

        CourseCreateRequest request = new CourseCreateRequest();
        request.setTitle("Kafka 课程");
        request.setCover("course/cover.png");
        request.setIntro("课程简介");
        request.setServiceContent("课程服务");
        request.setPrice(new BigDecimal("19.90"));
        request.setTPrice(new BigDecimal("99.90"));
        request.setType("media");
        request.setFreeType(0);
        request.setAfterServiceDays(30);
        request.setResourceType("FREE");
        request.setLevel(2);
        request.setTags(Collections.singletonList("Kafka"));

        OshUser operator = new OshUser();
        operator.setUsername("tester");

        Long courseId = oshCourseService.createCourse(request, operator);

        ArgumentCaptor<CourseIndexUpsertMessage> captor = ArgumentCaptor.forClass(CourseIndexUpsertMessage.class);
        verify(courseIndexKafkaProducer).sendCourseIndexCreate(captor.capture());
        assertEquals(Long.valueOf(123L), courseId);
        assertEquals(Long.valueOf(123L), captor.getValue().getCourseId());
        assertEquals("Kafka 课程", captor.getValue().getTitle());
        assertEquals("FREE", captor.getValue().getResourceType());
        assertEquals("Kafka", captor.getValue().getTagNames().get(0));
    }

    @Test
    public void shouldSendCourseIndexUpdateMessageAfterUpdateCourse() {
        OshCourse existingCourse = new OshCourse();
        existingCourse.setId(123L);
        existingCourse.setCreateBy("tester");

        OshCourse latestCourse = new OshCourse();
        latestCourse.setId(123L);
        latestCourse.setTitle("更新后的课程");
        latestCourse.setCover("course/cover-new.png");
        latestCourse.setIntro("更新后的课程简介");
        latestCourse.setServiceContent("更新后的服务");
        latestCourse.setType("media");

        when(oshCourseMapper.selectCourseById(123L)).thenReturn(existingCourse, latestCourse);
        when(oshCourseMapper.updateCourse(any(OshCourse.class))).thenReturn(1);

        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setId(123L);
        request.setTitle("更新后的课程");
        request.setCover("course/cover-new.png");
        request.setIntro("更新后的课程简介");
        request.setServiceContent("更新后的服务");
        request.setPrice(new BigDecimal("29.90"));
        request.setTPrice(new BigDecimal("129.90"));
        request.setType("media");
        request.setTags(Collections.emptyList());

        OshUser operator = new OshUser();
        operator.setUsername("tester");

        Long courseId = oshCourseService.updateCourse(request, operator);

        ArgumentCaptor<CourseIndexUpsertMessage> captor = ArgumentCaptor.forClass(CourseIndexUpsertMessage.class);
        verify(courseIndexKafkaProducer).sendCourseIndexUpdate(captor.capture());
        verify(oshCourseMapper, times(2)).selectCourseById(123L);
        assertEquals(Long.valueOf(123L), courseId);
        assertEquals(Long.valueOf(123L), captor.getValue().getCourseId());
        assertEquals("更新后的课程", captor.getValue().getTitle());
    }

    @Test
    public void shouldNormalizeTagNamesWhenSendingCourseIndexCreateMessage() {
        doAnswer(invocation -> {
            OshCourse course = invocation.getArgument(0);
            course.setId(123L);
            return 1;
        }).when(oshCourseMapper).insertCourse(any(OshCourse.class));
        doAnswer(invocation -> {
            com.backstage.system.domain.course.OshCourseTag courseTag = invocation.getArgument(0);
            courseTag.setId(88L);
            return 1;
        }).when(oshCourseTagMapper).insertCourseTag(any(com.backstage.system.domain.course.OshCourseTag.class));

        CourseCreateRequest request = new CourseCreateRequest();
        request.setTitle("Kafka 课程");
        request.setCover("course/cover.png");
        request.setIntro("课程简介");
        request.setServiceContent("课程服务");
        request.setPrice(new BigDecimal("19.90"));
        request.setTPrice(new BigDecimal("99.90"));
        request.setType("media");
        request.setTags(Arrays.asList(" Kafka ", "Spring", "Kafka", "", null, " Spring "));

        OshUser operator = new OshUser();
        operator.setUsername("tester");

        oshCourseService.createCourse(request, operator);

        ArgumentCaptor<CourseIndexUpsertMessage> captor = ArgumentCaptor.forClass(CourseIndexUpsertMessage.class);
        verify(courseIndexKafkaProducer, times(1)).sendCourseIndexCreate(captor.capture());
        assertEquals(Arrays.asList("Kafka", "Spring"), captor.getValue().getTagNames());
        assertEquals("Kafka Spring", captor.getValue().getTagNamesText());
    }
}
