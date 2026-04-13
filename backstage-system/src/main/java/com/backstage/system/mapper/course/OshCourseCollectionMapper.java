package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseCollection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshCourseCollectionMapper {

    OshCourseCollection selectByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    int insertCourseCollection(OshCourseCollection collection);

    int updateCollectionDeleteFlag(@Param("id") Long id,
                                   @Param("deleteFlag") Integer deleteFlag,
                                   @Param("operator") String operator);


}
