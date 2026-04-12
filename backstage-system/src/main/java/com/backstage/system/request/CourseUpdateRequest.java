package com.backstage.system.request;

import com.backstage.system.domain.course.vo.OshCourseTagSimpleVo;

/**
 * 课程修改请求
 */
public class CourseUpdateRequest extends CourseCreateRequest {

    @NotNull(message = "课程ID不能为空")
    private Long id;
import java.math.BigDecimal;
import java.util.List;

public class CourseUpdateRequest {
    public Long getId() {
        return null;
    }

    public CourseMaterialCreateRequest getMaterial() {
        return null;
    }

    public List<OshCourseTagSimpleVo> getTags() {
        return null;
    }

    public String getTitle() {
        return null;
    }

    public String getCover() {
        return null;
    }

    public String getIntro() {
        return null;
    }

    public String getServiceContent() {
        return null;
    }

    public BigDecimal getPrice() {
        return null;
    }

    public BigDecimal getTPrice() {
        return null;
    }

    public String getType() {
        return null;
    }

    public Integer getFreeType() {
        return null;
    }

    public Integer getAfterServiceDays() {
        return null;
    }

    public Integer getExamId() {
        return null;
    }

    public String getRemark() {
        return null;
    }

    public Integer getResourceType() {
        return null;
    }

    public Integer getLevel() {
        return null;
    }
}
