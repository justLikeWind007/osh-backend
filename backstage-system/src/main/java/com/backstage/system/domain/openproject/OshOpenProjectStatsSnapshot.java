package com.backstage.system.domain.openproject;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 开源项目每日数据快照
 * 继承 OSHBaseEntity 获得 createTime/createBy/updateTime/updateBy/deleteFlag 五个通用字段
 */
@TableName("osh_open_project_stats_snapshot")
public class OshOpenProjectStatsSnapshot extends OSHBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 开源项目ID */
    private Long projectId;

    /** 当天 Star 数 */
    private Integer starCount;

    /** 当天 Fork 数 */
    private Integer forkCount;

    /** 快照日期（精确到天） */
    private LocalDate snapshotDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Integer getStarCount() { return starCount; }
    public void setStarCount(Integer starCount) { this.starCount = starCount; }

    public Integer getForkCount() { return forkCount; }
    public void setForkCount(Integer forkCount) { this.forkCount = forkCount; }

    public LocalDate getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(LocalDate snapshotDate) { this.snapshotDate = snapshotDate; }
}
