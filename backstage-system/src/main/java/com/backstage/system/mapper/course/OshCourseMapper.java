package com.backstage.system.mapper.course;

<<<<<<<< HEAD:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCouresMapper.java
import com.backstage.system.domain.course.OshCoures;
========
import com.backstage.system.domain.course.OshCourse;
>>>>>>>> refs/heads/release/20260328:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCourseMapper.java
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程信息 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-01-XX
 */
@Mapper
<<<<<<<< HEAD:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCouresMapper.java
public interface OshCouresMapper 
========
public interface OshCourseMapper
>>>>>>>> refs/heads/release/20260328:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCourseMapper.java
{
    /**
     * 查询课程信息
     *
     * @param id 课程 ID
     * @return 课程信息
     */
<<<<<<<< HEAD:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCouresMapper.java
    OshCoures selectCourseById(Long id);
========
    OshCourse selectCourseById(Long id);
>>>>>>>> refs/heads/release/20260328:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCourseMapper.java

    /**
     * 根据 appid 和专栏 ID 查询课程列表
     *
     * @param columnId 专栏 ID
     * @return 课程列表
     */
<<<<<<<< HEAD:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCouresMapper.java
    List<OshCoures> selectCourseList(@Param("columnId") Long columnId);
========
    List<OshCourse> selectCourseList(@Param("columnId") Long columnId);
>>>>>>>> refs/heads/release/20260328:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCourseMapper.java

    /**
     * 新增课程
     *
     * @param course 课程信息
     * @return 结果
     */
<<<<<<<< HEAD:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCouresMapper.java
    int insertCourse(OshCoures course);
========
    int insertCourse(OshCourse course);
>>>>>>>> refs/heads/release/20260328:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCourseMapper.java

    /**
     * 修改课程信息
     *
     * @param course 课程信息
     * @return 结果
     */
<<<<<<<< HEAD:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCouresMapper.java
    int updateCourse(OshCoures course);
========
    int updateCourse(OshCourse course);
>>>>>>>> refs/heads/release/20260328:backstage-system/src/main/java/com/backstage/system/mapper/course/OshCourseMapper.java

    /**
     * 删除课程
     *
     * @param id 课程 ID
     * @return 结果
     */
    int deleteCourseById(Long id);

    /**
     * 批量删除课程
     *
     * @param ids 需要删除的数据 ID
     * @return 结果
     */
    int deleteCourseByIds(Long[] ids);
}
