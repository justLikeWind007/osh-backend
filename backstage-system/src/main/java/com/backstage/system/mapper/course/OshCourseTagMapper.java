package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseTag;
import com.backstage.system.domain.course.OshCourseTagRel;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程标签 Mapper 接口
 *
 * @author ruoyi
 * @date 2026-03-24
 */

@Mapper
public interface OshCourseTagMapper {

    OshCourseTag selectCourseTagByName(@Param("name") String name);

    int insertCourseTag(OshCourseTag tag);

    int insertCourseTagRel(OshCourseTagRel rel);

    int increaseUseCount(@Param("id") Long id);

    /**
     * 查询所有标签
     */
    @MapKey("id")
    List<Map<String, Object>> selectAllTags();

    /**
     * 根据关键字模糊查询标签
     * 按使用次数降序排列，返回前20条
     *
     * @param keyword 关键字（可选）
     * @return 标签列表
     */
    @MapKey("id")
    List<Map<String, Object>> selectTagsByKeyword(@Param("keyword") String keyword);

    /**
     * 查询课程的标签列表
     */
    @MapKey("id")
    List<Map<String, Object>> selectTagsByCourseId(@Param("courseId") Long courseId);

    /**
     * 新增标签
     *
     * @param tag 标签对象
     * @return 结果
     */
    int insertTag(OshCourseTag tag);

    /**
     * 检查标签名称是否已存在
     *
     * @param name 标签名称
     * @return 数量
     */
    int checkTagNameExists(@Param("name") String name);

    /**
     * 新增标签关联
     */
    int insertCourseTagRelation(Map<String, Object> params);

    /**
     * 删除标签关联（按课程 ID）
     */
    int deleteCourseTagRelationByCourseId(@Param("courseId") Long courseId);

    /**
     * 删除标签关联（按标签 ID）
     */
    int deleteCourseTagRelationByTagId(@Param("tagId") Long tagId);

    /**
     * 增加标签使用次数
     */
    int incrementUsageCount(@Param("tagId") Long tagId);
}
