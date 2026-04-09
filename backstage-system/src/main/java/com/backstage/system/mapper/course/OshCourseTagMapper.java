package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseTag;
import com.backstage.system.domain.course.OshCourseTagRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshCourseTagMapper {

    OshCourseTag selectCourseTagByName(@Param("name") String name);

    int insertCourseTag(OshCourseTag tag);

    int insertCourseTagRel(OshCourseTagRel rel);

    int increaseUseCount(@Param("id") Long id);
}
