package com.backstage.system.mapper.course;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程服务人员 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
public interface OshCourseStaffMapper {
    
    /**
     * 查询课程服务人员列表
     */
    List<Map<String, Object>> selectStaffsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 查询用户是否为课程服务人员
     */
    Map<String, Object> selectStaffByUserIdAndCourseId(
        @Param("userId") Long userId,
        @Param("courseId") Long courseId
    );
    
    /**
     * 新增服务人员申请
     */
    int insertStaff(Map<String, Object> params);
    
    /**
     * 审核服务人员申请
     */
    int updateStaffAudit(Map<String, Object> params);
    
    /**
     * 删除服务人员记录
     */
    int deleteStaffById(@Param("id") Long id);
    
    /**
     * 批量删除服务人员记录
     */
    int deleteStaffsByCourseId(@Param("courseId") Long courseId);
}
