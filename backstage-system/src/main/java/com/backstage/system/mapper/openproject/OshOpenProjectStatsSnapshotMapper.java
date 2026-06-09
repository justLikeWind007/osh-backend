package com.backstage.system.mapper.openproject;

import com.backstage.system.domain.openproject.OshOpenProjectStatsSnapshot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OshOpenProjectStatsSnapshotMapper extends BaseMapper<OshOpenProjectStatsSnapshot> {

    /**
     * 查询指定日期的所有项目快照
     */
    @Select("SELECT * FROM osh_open_project_stats_snapshot WHERE snapshot_date = #{date} AND delete_flag = 0")
    List<OshOpenProjectStatsSnapshot> selectByDate(@Param("date") LocalDate date);

    /**
     * 查询指定项目在指定日期的快照
     */
    @Select("SELECT * FROM osh_open_project_stats_snapshot WHERE project_id = #{projectId} AND snapshot_date = #{date} AND delete_flag = 0 LIMIT 1")
    OshOpenProjectStatsSnapshot selectByProjectAndDate(@Param("projectId") Long projectId, @Param("date") LocalDate date);
}
