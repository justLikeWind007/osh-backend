package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseCollection;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface OshCourseCollectionMapper extends BaseMapper<OshCourseCollection> {

    OshCourseCollection selectByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    int insertCourseCollection(OshCourseCollection collection);

    int updateCollectionDeleteFlag(@Param("id") Long id,
                                   @Param("deleteFlag") Integer deleteFlag,
                                   @Param("operator") String operator);

    default List<Long> selectActiveCourseIdsByUserIdAndCourseIds(Long userId, List<Long> courseIds) {
        if (userId == null || courseIds == null || courseIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<OshCourseCollection> queryWrapper = new LambdaQueryWrapper<OshCourseCollection>()
                .select(OshCourseCollection::getCourseId)
                .eq(OshCourseCollection::getUserId, userId)
                .eq(OshCourseCollection::getDeleteFlag, 0)
                .in(OshCourseCollection::getCourseId, courseIds);

        return selectList(queryWrapper).stream()
                .map(OshCourseCollection::getCourseId)
                .collect(Collectors.toList());
    }

    /**
     * 查询用户所有未删除的课程ID列表
     *
     * @param userId 用户ID
     * @return 未删除的课程ID列表，按创建时间和ID倒序排列
     */
    default List<Long> selectActiveCourseIdsByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<OshCourseCollection> queryWrapper = new LambdaQueryWrapper<OshCourseCollection>()
                .select(OshCourseCollection::getCourseId)
                .eq(OshCourseCollection::getUserId, userId)
                .eq(OshCourseCollection::getDeleteFlag, 0)
                .orderByDesc(OshCourseCollection::getCreateTime)
                .orderByDesc(OshCourseCollection::getId);

        return selectList(queryWrapper).stream()
                .map(OshCourseCollection::getCourseId)
                .collect(Collectors.toList());
    }

}
