package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 首页热门答疑 VO
 *
 * @author jayTatum
 */
@ApiModel(description = "首页热门答疑")
public class HotQaVO {

    @ApiModelProperty("问题ID")
    private Long id;

    @ApiModelProperty("问题内容（截取前50字）")
    private String content;

    @ApiModelProperty("资源类型：0=无，1=网站，2=课程，3=电子书，4=其他")
    private String resourceType;

    @ApiModelProperty("状态：0=待回答，1=已解决，2=已关闭")
    private Integer status;

    @ApiModelProperty("浏览量")
    private Integer viewCount;

    @ApiModelProperty("关注数")
    private Integer followCount;

    @ApiModelProperty("回答数")
    private Integer answerCount;

    @ApiModelProperty("提问者用户名")
    private String user;

    @ApiModelProperty("发布时间（相对描述）")
    private String time;

    @ApiModelProperty("详情页跳转路径")
    private String detailUrl;

    // ========== getter / setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getFollowCount() { return followCount; }
    public void setFollowCount(Integer followCount) { this.followCount = followCount; }

    public Integer getAnswerCount() { return answerCount; }
    public void setAnswerCount(Integer answerCount) { this.answerCount = answerCount; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDetailUrl() { return detailUrl; }
    public void setDetailUrl(String detailUrl) { this.detailUrl = detailUrl; }
}
