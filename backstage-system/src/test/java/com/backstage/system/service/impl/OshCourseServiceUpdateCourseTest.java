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
import com.backstage.system.request.CourseUpdateRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OshCourseServiceUpdateCourseTest {

    @Test
    public void shouldUpdateCourseMainFieldsAndRebuildMaterialAndTags() {
        OshCourseMapper courseMapper = mock(OshCourseMapper.class);
        OshCourseTagMapper courseTagMapper = mock(OshCourseTagMapper.class);
        OshCourseMaterialMapper courseMaterialMapper = mock(OshCourseMaterialMapper.class);
        OshCourseServiceImpl service = new OshCourseServiceImpl();
        ReflectionTestUtils.setField(service, "oshCourseMapper", courseMapper);
        ReflectionTestUtils.setField(service, "oshCourseTagMapper", courseTagMapper);
        ReflectionTestUtils.setField(service, "oshCourseMaterialMapper", courseMaterialMapper);

        CourseUpdateRequest request = buildUpdateRequest();

        User operator = new User();
        operator.setUsername("course_editor");

        OshCourse existingCourse = new OshCourse();
        existingCourse.setId(2001L);
        existingCourse.setCreateBy("course_editor");
        when(courseMapper.selectCourseById(2001L)).thenReturn(existingCourse);
        when(courseMapper.updateCourse(any(OshCourse.class))).thenReturn(1);
        when(courseMaterialMapper.deleteMaterialsByCourseId(2001L)).thenReturn(1);
        when(courseMaterialMapper.insertMaterialEntity(any(OshCourseMaterial.class))).thenReturn(1);
        when(courseTagMapper.deleteCourseTagRelationByCourseId(2001L)).thenReturn(2);

        OshCourseTag existingTag = new OshCourseTag();
        existingTag.setId(41L);
        existingTag.setName("考研");
        when(courseTagMapper.selectCourseTagByName("考研")).thenReturn(existingTag);
        when(courseTagMapper.selectCourseTagByName("就业")).thenReturn(null);
        when(courseTagMapper.insertCourseTag(any(OshCourseTag.class))).thenAnswer(invocation -> {
            OshCourseTag tag = invocation.getArgument(0);
            tag.setId(42L);
            return 1;
        });
        when(courseTagMapper.insertCourseTagRel(any(OshCourseTagRel.class))).thenReturn(1);
        when(courseTagMapper.increaseUseCount(anyLong())).thenReturn(1);

        Long courseId = service.updateCourse(request, operator);

        Assert.assertEquals(Long.valueOf(2001L), courseId);
        verify(courseMapper, times(1)).updateCourse(argThat(course ->
                Long.valueOf(2001L).equals(course.getId())
                        && "Java 就业班".equals(course.getTitle())
                        && "https://cdn.example.com/new-cover.png".equals(course.getCover())
                        && "系统班升级版".equals(course.getIntro())
                        && "1v1 答疑".equals(course.getServiceContent())
                        && new BigDecimal("299.00").compareTo(course.getPrice()) == 0
                        && new BigDecimal("399.00").compareTo(course.getTPrice()) == 0
                        && "media".equals(course.getType())
                        && Integer.valueOf(1).equals(course.getFreeType())
                        && Integer.valueOf(180).equals(course.getAfterServiceDays())
                        && Integer.valueOf(18).equals(course.getExamId())
                        && "更新备注".equals(course.getRemark())
                        && Integer.valueOf(2).equals(course.getResourceType())
                        && Integer.valueOf(3).equals(course.getLevel())
                        && "course_editor".equals(course.getUpdateBy())));
        verify(courseMaterialMapper, times(1)).deleteMaterialsByCourseId(2001L);
        verify(courseMaterialMapper, times(1)).insertMaterialEntity(argThat(material ->
                Long.valueOf(2001L).equals(material.getCourseId())
                        && "资料包".equals(material.getMaterialName())
                        && "https://cdn.example.com/material.zip".equals(material.getFileUrl())
                        && "zip".equals(material.getFileType())
                        && Long.valueOf(2L).equals(material.getFileSize())
                        && "course_editor".equals(material.getCreateBy())
                        && "course_editor".equals(material.getUpdateBy())));
        verify(courseTagMapper, times(1)).deleteCourseTagRelationByCourseId(2001L);
        verify(courseTagMapper, times(1)).selectCourseTagByName("考研");
        verify(courseTagMapper, times(1)).selectCourseTagByName("就业");
        verify(courseTagMapper, times(1)).insertCourseTag(any(OshCourseTag.class));
        verify(courseTagMapper, times(2)).insertCourseTagRel(any(OshCourseTagRel.class));
        verify(courseTagMapper, times(1)).increaseUseCount(41L);
        verify(courseTagMapper, times(1)).increaseUseCount(42L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUpdateWhenCourseDoesNotExist() {
        OshCourseMapper courseMapper = mock(OshCourseMapper.class);
        OshCourseTagMapper courseTagMapper = mock(OshCourseTagMapper.class);
        OshCourseMaterialMapper courseMaterialMapper = mock(OshCourseMaterialMapper.class);
        OshCourseServiceImpl service = new OshCourseServiceImpl();
        ReflectionTestUtils.setField(service, "oshCourseMapper", courseMapper);
        ReflectionTestUtils.setField(service, "oshCourseTagMapper", courseTagMapper);
        ReflectionTestUtils.setField(service, "oshCourseMaterialMapper", courseMaterialMapper);

        when(courseMapper.selectCourseById(2001L)).thenReturn(null);

        service.updateCourse(buildUpdateRequestWithoutTagsAndMaterial(), new User());

        verify(courseMapper, never()).updateCourse(any(OshCourse.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUpdateWhenOperatorIsNotCourseCreator() {
        OshCourseMapper courseMapper = mock(OshCourseMapper.class);
        OshCourseTagMapper courseTagMapper = mock(OshCourseTagMapper.class);
        OshCourseMaterialMapper courseMaterialMapper = mock(OshCourseMaterialMapper.class);
        OshCourseServiceImpl service = new OshCourseServiceImpl();
        ReflectionTestUtils.setField(service, "oshCourseMapper", courseMapper);
        ReflectionTestUtils.setField(service, "oshCourseTagMapper", courseTagMapper);
        ReflectionTestUtils.setField(service, "oshCourseMaterialMapper", courseMaterialMapper);

        OshCourse existingCourse = new OshCourse();
        existingCourse.setId(2001L);
        existingCourse.setCreateBy("course_creator");
        when(courseMapper.selectCourseById(2001L)).thenReturn(existingCourse);

        User operator = new User();
        operator.setUsername("other_editor");

        service.updateCourse(buildUpdateRequestWithoutTagsAndMaterial(), operator);

        verify(courseMapper, never()).updateCourse(any(OshCourse.class));
        verify(courseMaterialMapper, never()).deleteMaterialsByCourseId(anyLong());
        verify(courseTagMapper, never()).deleteCourseTagRelationByCourseId(anyLong());
    }

    private CourseUpdateRequest buildUpdateRequest() {
        CourseUpdateRequest request = buildUpdateRequestWithoutTagsAndMaterial();
        request.setTags(Arrays.asList(
                new OshCourseTagSimpleVo(null, "  考研 ", 8),
                new OshCourseTagSimpleVo(null, "就业", 5)
        ));

        CourseMaterialCreateRequest material = new CourseMaterialCreateRequest();
        material.setFileName("  资料包 ");
        material.setFileUrl(" https://cdn.example.com/material.zip ");
        material.setFileType(" zip ");
        material.setFileSize(new BigDecimal("1536"));
        request.setMaterial(material);
        return request;
    }

    private CourseUpdateRequest buildUpdateRequestWithoutTagsAndMaterial() {
        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setId(2001L);
        request.setTitle("  Java 就业班 ");
        request.setCover(" https://cdn.example.com/new-cover.png ");
        request.setIntro(" 系统班升级版 ");
        request.setServiceContent(" 1v1 答疑 ");
        request.setPrice(new BigDecimal("299.00"));
        request.setTPrice(new BigDecimal("399.00"));
        request.setType("media");
        request.setFreeType(1);
        request.setAfterServiceDays(180);
        request.setExamId(18);
        request.setRemark(" 更新备注 ");
        request.setResourceType(2);
        request.setLevel(3);
        return request;
    }
}
