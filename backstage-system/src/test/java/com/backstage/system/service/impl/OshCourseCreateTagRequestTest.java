package com.backstage.system.service.impl;

import com.backstage.system.domain.course.vo.OshCourseTagSimpleVo;
import com.backstage.system.request.CourseCreateRequest;
import com.backstage.system.request.CourseMaterialCreateRequest;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.Set;
import javax.validation.ConstraintViolation;

public class OshCourseCreateTagRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

    @Test
    public void shouldTrimCourseMaterialFieldsWhenSettingCourseCreateRequestMaterial() {
        CourseCreateRequest request = new CourseCreateRequest();
        CourseMaterialCreateRequest material = new CourseMaterialCreateRequest();
        material.setFileName("  资料包  ");
        material.setFileUrl("  https://cdn.example.com/material.zip  ");
        material.setFileType("  zip  ");

        request.setMaterial(material);

        Assert.assertEquals("资料包", request.getMaterial().getFileName());
        Assert.assertEquals("https://cdn.example.com/material.zip", request.getMaterial().getFileUrl());
        Assert.assertEquals("zip", request.getMaterial().getFileType());
    }

    @Test
    public void shouldRejectNonArchiveFileTypeForCourseMaterial() {
        CourseMaterialCreateRequest material = new CourseMaterialCreateRequest();
        material.setFileName("资料包");
        material.setFileUrl("https://cdn.example.com/material.pdf");
        material.setFileType("pdf");
        material.setFileSize(java.math.BigDecimal.ONE);

        Set<ConstraintViolation<CourseMaterialCreateRequest>> violations = validator.validate(material);

        Assert.assertTrue(violations.stream().anyMatch(item -> "资料文件类型必须是压缩包类型".equals(item.getMessage())));
    }
}
