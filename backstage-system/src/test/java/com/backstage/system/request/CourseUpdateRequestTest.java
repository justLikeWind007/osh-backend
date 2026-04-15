package com.backstage.system.request;

import com.backstage.system.domain.course.vo.OshCourseTagSimpleVo;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

public class CourseUpdateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldTrimFieldsWhenSettingCourseUpdateRequest() {
        CourseUpdateRequest request = new CourseUpdateRequest();
        OshCourseTagSimpleVo first = new OshCourseTagSimpleVo();
        first.setName("  考研  ");
        OshCourseTagSimpleVo second = new OshCourseTagSimpleVo();
        second.setName(" ");

        request.setId(1L);
        request.setTitle("  课程标题  ");
        request.setCover("  https://cdn.example.com/cover.png  ");
        request.setIntro("  课程介绍  ");
        request.setServiceContent("  服务内容  ");
        request.setType("  video  ");
        request.setRemark("  备注  ");
        request.setPrice(new BigDecimal("99.00"));
        request.setTPrice(new BigDecimal("199.00"));
        request.setTags(Arrays.asList(first, second));

        Assert.assertEquals(Long.valueOf(1L), request.getId());
        Assert.assertEquals("课程标题", request.getTitle());
        Assert.assertEquals("https://cdn.example.com/cover.png", request.getCover());
        Assert.assertEquals("课程介绍", request.getIntro());
        Assert.assertEquals("服务内容", request.getServiceContent());
        Assert.assertEquals("video", request.getType());
        Assert.assertEquals("备注", request.getRemark());
        Assert.assertEquals("考研", request.getTags().get(0).getName());
        Assert.assertNull(request.getTags().get(1).getName());
    }

    @Test
    public void shouldRejectMissingIdAndBlankRequiredFields() {
        CourseUpdateRequest request = new CourseUpdateRequest();
        request.setPrice(BigDecimal.ZERO);
        request.setTPrice(BigDecimal.ZERO);

        Set<ConstraintViolation<CourseUpdateRequest>> violations = validator.validate(request);

        Assert.assertTrue(violations.stream().anyMatch(item -> "课程id不能为空".equals(item.getMessage())));
        Assert.assertTrue(violations.stream().anyMatch(item -> "课程标题不能为空".equals(item.getMessage())));
        Assert.assertTrue(violations.stream().anyMatch(item -> "课程封面不能为空".equals(item.getMessage())));
        Assert.assertTrue(violations.stream().anyMatch(item -> "课程介绍不能为空".equals(item.getMessage())));
        Assert.assertTrue(violations.stream().anyMatch(item -> "课程类型不能为空".equals(item.getMessage())));
    }
}
