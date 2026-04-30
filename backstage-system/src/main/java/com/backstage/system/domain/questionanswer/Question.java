package com.backstage.system.domain.questionanswer;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:17
 */
@TableName("osh_question_answer_question")
public class Question extends OSHBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 提问者id
     */
    private Long userId;

    /**
     * 资源类型：0=无，1=网站，2=课程，3=电子书，4=其他
     */
    private String resourceType;

    /**
     * 资源编号（resource_type=0时为空）
     */
    private Long resourceNo;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 是否仅付费用户专属答疑：0=普通免费，1=付费专属
     */
    private Byte isPaidOnly;

    /**
     * 权限等级
     */
    private Integer level;

    /**
     * 状态：0=待回答，1=已解决，2=已关闭
     */
    private Byte status;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 关注数
     */
    private Integer followCount;
    /**
     * 删除标志：0=存在，1=删除
     */
    @TableLogic
    private Byte deleteFlag;

    // 别忘了写 Getter 和 Setter
    public Byte getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Byte deleteFlag) {
        this.deleteFlag = deleteFlag;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(Long resourceNo) {
        this.resourceNo = resourceNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Byte getIsPaidOnly() {
        return isPaidOnly;
    }

    public void setIsPaidOnly(Byte isPaidOnly) {
        this.isPaidOnly = isPaidOnly;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", userId=" + userId +
                ", resourceType=" + resourceType +
                ", resourceId=" + resourceNo +
                ", content='" + content + '\'' +
                ", isPaidOnly=" + isPaidOnly +
                ", level=" + level +
                ", status=" + status +
                ", viewCount=" + viewCount +
                ", followCount=" + followCount +
                '}';
    }
}