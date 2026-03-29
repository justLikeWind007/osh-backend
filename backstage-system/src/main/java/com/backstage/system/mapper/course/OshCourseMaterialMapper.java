package com.backstage.system.mapper.course;

import com.backstage.system.domain.course.OshCourseMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程资料 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
public interface OshCourseMaterialMapper {
    
    /**
     * 查询资料列表
     */
    List<Map<String, Object>> selectMaterialsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 查询资料详情
     */
    Map<String, Object> selectMaterialById(@Param("id") Long id);
    
    /**
     * 新增资料
     */
    int insertMaterial(Map<String, Object> params);
    
    /**
     * 新增资料（使用实体对象）
     * @param material 资料实体
     * @return 结果
     */
    int insertMaterialEntity(OshCourseMaterial material);
    
    /**
     * 修改资料
     */
    int updateMaterial(Map<String, Object> params);
    
    /**
     * 删除资料
     */
    int deleteMaterialById(@Param("id") Long id);
    
    /**
     * 批量删除资料
     */
    int deleteMaterialsByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 增加下载次数
     */
    int incrementDownloadCount(@Param("id") Long id);
}
