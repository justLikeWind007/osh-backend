package com.backstage.system.domain.openproject;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 开源项目表
 */
@TableName("osh_open_project")
public class OshOpenProject extends OSHBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 项目名称 */
    private String projectName;

    /** 项目描述 */
    private String projectDesc;

    /** 项目链接（Gitee/GitHub） */
    private String projectUrl;

    /** 作者名称 */
    private String authorName;

    /** 封面图片 URL */
    private String projectCover;

    /** 状态：0-待审核，1-已通过，2-已拒绝 */
    private Integer status;

    /** 点击次数 */
    private Integer clickCount;

    /** 拒绝原因 */
    private String rejectReason;

    /** GitHub Star 数 */
    private Integer starCount;

    /** GitHub Fork 数 */
    private Integer forkCount;

    /** 最近一次提交时间（从 GitHub 同步） */
    private LocalDateTime lastCommitTime;

    /** 是否已归档：0-活跃，1-已归档 */
    private Byte isArchived;

    /** 最后一次从 GitHub 同步数据的时间 */
    private LocalDateTime lastSyncTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getProjectDesc() { return projectDesc; }
    public void setProjectDesc(String projectDesc) { this.projectDesc = projectDesc; }

    public String getProjectUrl() { return projectUrl; }
    public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getProjectCover() { return projectCover; }
    public void setProjectCover(String projectCover) { this.projectCover = projectCover; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getClickCount() { return clickCount; }
    public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public Integer getStarCount() { return starCount; }
    public void setStarCount(Integer starCount) { this.starCount = starCount; }

    public Integer getForkCount() { return forkCount; }
    public void setForkCount(Integer forkCount) { this.forkCount = forkCount; }

    public LocalDateTime getLastCommitTime() { return lastCommitTime; }
    public void setLastCommitTime(LocalDateTime lastCommitTime) { this.lastCommitTime = lastCommitTime; }

    public Byte getIsArchived() { return isArchived; }
    public void setIsArchived(Byte isArchived) { this.isArchived = isArchived; }

    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
