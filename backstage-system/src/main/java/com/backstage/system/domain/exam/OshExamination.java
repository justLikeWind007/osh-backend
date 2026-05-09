package com.backstage.system.domain.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 考试/试卷主表实体
 */
@TableName("osh_examination")
public class OshExamination implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Integer totalScore;
    private Integer passScore;
    private Integer expire;
    private Integer questionCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private Integer status;
    /** 关联资源类型：course / book / null */
    private String resourceType;
    /** 关联资源ID */
    private Long resourceId;
    /** 封面图 */
    private String cover;
    /** 考试描述 */
    private String description;
    /** 收藏数 */
    private Integer collectCount;
    private String createBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /** 逻辑删除：0-未删除，1-已删除 */
    @TableField("delete_flag")
    private Integer deleteFlag;

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
    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
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
    public Integer getCollectCount() { return collectCount; }
    public void setCollectCount(Integer collectCount) { this.collectCount = collectCount; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }
}
