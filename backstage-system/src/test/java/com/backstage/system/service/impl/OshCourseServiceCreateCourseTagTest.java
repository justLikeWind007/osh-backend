package com.backstage.system.service.impl;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.OshCourseMaterial;
import com.backstage.system.domain.course.OshCourseTag;
import com.backstage.system.domain.course.OshCourseTagRel;
import com.backstage.system.domain.course.vo.OshCourseTagSimpleVo;
import com.backstage.system.domain.user.User;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseMaterialMapper;
import com.backstage.system.mapper.course.OshCourseTagMapper;
import com.backstage.system.request.CourseMaterialCreateRequest;
import com.backstage.system.request.CourseCreateRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OshCourseServiceCreateCourseTagTest {

    @Test
    public void shouldCreateMissingTagsAndInsertRelationsWhenCreatingCourse() {
        OshCourseMapper courseMapper = mock(OshCourseMapper.class);
        OshCourseTagMapper courseTagMapper = mock(OshCourseTagMapper.class);
        OshCourseMaterialMapper courseMaterialMapper = mock(OshCourseMaterialMapper.class);
        OshCourseServiceImpl service = new OshCourseServiceImpl();
        ReflectionTestUtils.setField(service, "oshCourseMapper", courseMapper);
        ReflectionTestUtils.setField(service, "oshCourseTagMapper", courseTagMapper);
        ReflectionTestUtils.setField(service, "oshCourseMaterialMapper", courseMaterialMapper);

        CourseCreateRequest request = buildCreateRequest();
        request.setTags(Arrays.asList(
                new OshCourseTagSimpleVo(null, "  考研 ", 9),
                new OshCourseTagSimpleVo(null, "考研", 3),
                new OshCourseTagSimpleVo(null, "就业", 2),
                new OshCourseTagSimpleVo(null, " ", 5)
        ));

        User operator = new User();
        operator.setUsername("course_admin");

        when(courseMapper.insertCourse(any(OshCourse.class))).thenAnswer(invocation -> {
            OshCourse course = invocation.getArgument(0);
            course.setId(1001L);
            return 1;
        });

        OshCourseTag existingTag = new OshCourseTag();
        existingTag.setId(31L);
        existingTag.setName("考研");
        existingTag.setSort(9);
        when(courseTagMapper.selectCourseTagByName("考研")).thenReturn(existingTag);
        when(courseTagMapper.selectCourseTagByName("就业")).thenReturn(null);
        when(courseTagMapper.insertCourseTag(any(OshCourseTag.class))).thenAnswer(invocation -> {
            OshCourseTag tag = invocation.getArgument(0);
            tag.setId(32L);
            return 1;
        });
        when(courseTagMapper.insertCourseTagRel(any(OshCourseTagRel.class))).thenReturn(1);
        when(courseTagMapper.increaseUseCount(anyLong())).thenReturn(1);

        Long courseId = service.createCourse(request, operator);

        Assert.assertEquals(Long.valueOf(1001L), courseId);
        verify(courseTagMapper, times(1)).selectCourseTagByName("考研");
        verify(courseTagMapper, times(1)).selectCourseTagByName("就业");
        verify(courseTagMapper, times(1)).insertCourseTag(any(OshCourseTag.class));
        verify(courseTagMapper, times(2)).insertCourseTagRel(any(OshCourseTagRel.class));
        verify(courseTagMapper, times(1)).increaseUseCount(31L);
        verify(courseTagMapper, times(1)).increaseUseCount(32L);
        verify(courseMaterialMapper, never()).insertMaterialEntity(any(OshCourseMaterial.class));
    }

    @Test
    public void shouldSkipTagPersistenceWhenCourseCreateRequestHasNoUsableTags() {
        OshCourseMapper courseMapper = mock(OshCourseMapper.class);
        OshCourseTagMapper courseTagMapper = mock(OshCourseTagMapper.class);
        OshCourseMaterialMapper courseMaterialMapper = mock(OshCourseMaterialMapper.class);
        OshCourseServiceImpl service = new OshCourseServiceImpl();
        ReflectionTestUtils.setField(service, "oshCourseMapper", courseMapper);
        ReflectionTestUtils.setField(service, "oshCourseTagMapper", courseTagMapper);
        ReflectionTestUtils.setField(service, "oshCourseMaterialMapper", courseMaterialMapper);

        CourseCreateRequest request = buildCreateRequest();
        request.setTags(Arrays.asList(
                new OshCourseTagSimpleVo(null, " ", 1),
                new OshCourseTagSimpleVo(null, null, 2)
        ));

        when(courseMapper.insertCourse(any(OshCourse.class))).thenAnswer(invocation -> {
            OshCourse course = invocation.getArgument(0);
            course.setId(1002L);
            return 1;
        });

        Long courseId = service.createCourse(request, new User());

        Assert.assertEquals(Long.valueOf(1002L), courseId);
        verify(courseTagMapper, never()).selectCourseTagByName(anyString());
        verify(courseTagMapper, never()).insertCourseTag(any(OshCourseTag.class));
        verify(courseTagMapper, never()).insertCourseTagRel(any(OshCourseTagRel.class));
        verify(courseTagMapper, never()).increaseUseCount(anyLong());
        verify(courseMaterialMapper, never()).insertMaterialEntity(any(OshCourseMaterial.class));
    }

    @Test
    public void shouldInsertCourseMaterialWhenCreateRequestContainsMaterial() {
        OshCourseMapper courseMapper = mock(OshCourseMapper.class);
        OshCourseTagMapper courseTagMapper = mock(OshCourseTagMapper.class);
        OshCourseMaterialMapper courseMaterialMapper = mock(OshCourseMaterialMapper.class);
        OshCourseServiceImpl service = new OshCourseServiceImpl();
        ReflectionTestUtils.setField(service, "oshCourseMapper", courseMapper);
        ReflectionTestUtils.setField(service, "oshCourseTagMapper", courseTagMapper);
        ReflectionTestUtils.setField(service, "oshCourseMaterialMapper", courseMaterialMapper);

        CourseCreateRequest request = buildCreateRequest();
        CourseMaterialCreateRequest material = new CourseMaterialCreateRequest();
        material.setFileName("  课程资料压缩包  ");
        material.setFileUrl("  https://cdn.example.com/course-material.zip  ");
        material.setFileType("  zip  ");
        material.setFileSize(new BigDecimal("1536"));
        request.setMaterial(material);

        User operator = new User();
        operator.setUsername("course_admin");

        when(courseMapper.insertCourse(any(OshCourse.class))).thenAnswer(invocation -> {
            OshCourse course = invocation.getArgument(0);
            course.setId(1003L);
            return 1;
        });
        when(courseMaterialMapper.insertMaterialEntity(any(OshCourseMaterial.class))).thenReturn(1);

        Long courseId = service.createCourse(request, operator);

        Assert.assertEquals(Long.valueOf(1003L), courseId);
        verify(courseMaterialMapper, times(1)).insertMaterialEntity(any(OshCourseMaterial.class));
        verify(courseMaterialMapper, times(1)).insertMaterialEntity(argThat(materialEntity ->
                Long.valueOf(1003L).equals(materialEntity.getCourseId())
                        && "课程资料压缩包".equals(materialEntity.getMaterialName())
                        && "https://cdn.example.com/course-material.zip".equals(materialEntity.getFileUrl())
                        && "zip".equals(materialEntity.getFileType())
                        && Long.valueOf(2L).equals(materialEntity.getFileSize())
                        && "course_admin".equals(materialEntity.getCreateBy())
                        && "course_admin".equals(materialEntity.getUpdateBy())));
        verify(courseTagMapper, never()).insertCourseTag(any(OshCourseTag.class));
    }

    private CourseCreateRequest buildCreateRequest() {
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
        return request;
    }
}
