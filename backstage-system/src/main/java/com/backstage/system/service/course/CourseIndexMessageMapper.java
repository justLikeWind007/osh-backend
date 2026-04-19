package com.backstage.system.service.course;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.backstage.system.domain.course.OshCourse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseIndexMessageMapper
{
    @Mapping(target = "operator", source = "operatorName")
    CourseIndexUpsertMessage toMessage(OshCourse course, String operatorName);
}
