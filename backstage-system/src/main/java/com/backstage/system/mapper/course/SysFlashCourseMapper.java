package com.backstage.system.mapper.course;

import com.backstage.system.domain.vo.FlashCourseVo;

import java.util.List;

public interface SysFlashCourseMapper {

    /**
     * 查询课程详情
     *
     * @param id 课程详情主键
     * @return 课程详情
     */
    public FlashCourseVo selectOshCourseById(Long id);

    /**
     * 查询课程详情列表
     *
     * @param oshCourse 课程详情
     * @return 课程详情集合
     */
    public List<FlashCourseVo> selectOshCourseList(FlashCourseVo oshCourse);

    /**
     * 新增课程详情
     *
     * @param oshCourse 课程详情
     * @return 结果
     */
    public int insertOshCourse(FlashCourseVo oshCourse);

    /**
     * 修改课程详情
     *
     * @param oshCourse 课程详情
     * @return 结果
     */
    public int updateOshCourse(FlashCourseVo oshCourse);

    /**
     * 删除课程详情
     *
     * @param id 课程详情主键
     * @return 结果
     */
    public int deleteOshCourseById(Long id);

    /**
     * 批量删除课程详情
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOshCourseByIds(Long[] ids);

    List<FlashCourseVo> selectCoursesByColumnId(Long id);

}
