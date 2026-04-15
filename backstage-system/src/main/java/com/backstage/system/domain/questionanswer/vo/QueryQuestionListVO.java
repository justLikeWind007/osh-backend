package com.backstage.system.domain.questionanswer.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/29
 * Time: 18:14
 */
public class QueryQuestionListVO {
    /**
     * 主键id
     */
    @JsonSerialize(using = ToStringSerializer.class)
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
                ", status=" + status +
                ", viewCount=" + viewCount +
                ", followCount=" + followCount +
                '}';
    }
}
