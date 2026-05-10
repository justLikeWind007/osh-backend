package com.backstage.system.domain.dto.exam;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 新增/修改考试 DTO
 */
public class ExamSaveDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 考试ID（有值=修改，null=新增） */
    private Long id;
    private String title;
    private Integer totalScore;
    private Integer passScore;
    /** 考试时长（分钟） */
    private Integer expire;
    private Date startTime;
    private Date endTime;
    private Integer status;
    /** 关联资源类型：course / book / null */
    private String resourceType;
    /** 关联资源ID */
    private Long resourceId;
    private String cover;
    private String description;
    /** 标签名称列表（后端自动 resolve：存在复用，不存在新建） */
    private List<String> tags;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public Integer getPassScore() { return passScore; }
    public void setPassScore(Integer passScore) { this.passScore = passScore; }
    public Integer getExpire() { return expire; }
    public void setExpire(Integer expire) { this.expire = expire; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
