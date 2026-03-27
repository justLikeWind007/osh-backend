package com.backstage.system.domain.questionanswer;

import java.io.Serializable;
import java.util.Date;
/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:20
 */
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 所属问题id
     */
    private Long questionId;

    /**
     * 回答者id
     */
    private Long userId;

    /**
     * 回答内容
     */
    private String content;

    /**
     * 热度（点赞数/投票数）
     */
    private Integer voteCount;

    /**
     * 是否为提问者标记的已解决回答：0=否，1=是
     */
    private Byte isSolution;

    /**
     * 是否被标记为乱答/违规：0=否，1=是
     */
    private Byte isFlagged;

    /**
     * 状态：0=正常，1=已删除，2=违规锁定
     */
    private Byte status;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    // 无参构造方法
    public Answer() {
    }

    // 全参构造方法
    public Answer(Long id, Long questionId, Long userId, String content, Integer voteCount,
                  Byte isSolution, Byte isFlagged, Byte status, Date createdTime, Date updatedTime) {
        this.id = id;
        this.questionId = questionId;
        this.userId = userId;
        this.content = content;
        this.voteCount = voteCount;
        this.isSolution = isSolution;
        this.isFlagged = isFlagged;
        this.status = status;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Byte getIsSolution() {
        return isSolution;
    }

    public void setIsSolution(Byte isSolution) {
        this.isSolution = isSolution;
    }

    public Byte getIsFlagged() {
        return isFlagged;
    }

    public void setIsFlagged(Byte isFlagged) {
        this.isFlagged = isFlagged;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", voteCount=" + voteCount +
                ", isSolution=" + isSolution +
                ", isFlagged=" + isFlagged +
                ", status=" + status +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
