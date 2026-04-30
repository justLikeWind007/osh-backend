package com.backstage.system.domain.questionanswer;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:20
 */
@TableName("osh_question_answer_answer")
public class Answer extends OSHBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 权限等级
     */
    private Integer level;

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
     * 状态：0=正常，1=已删除，2=违规锁定
     */
    private Byte status;

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", userId=" + userId +
                ", level=" + level +
                ", content='" + content + '\'' +
                ", voteCount=" + voteCount +
                ", isSolution=" + isSolution +
                ", status=" + status +
                '}';
    }
}
