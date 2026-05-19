package com.backstage.system.domain.vo.info_gap;

import java.time.LocalDateTime;

/**
 * 列表展示对象 - 包含发布者信息和当前用户是否关注
 */
public class InfoGapVO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String tag;
    private Integer goodCount;
    private Integer middleCount;
    private Integer badCount;
    private Integer viewCount;
    private Integer collectCount;
    private java.time.LocalDateTime createTime;
    private java.time.LocalDateTime updateTime;

    // 发布者信息 (从 OshUser 表关联)
    private String nickname;
    private String avatar;
    // 扩展功能：当前登录用户是否已关注该作者
    private Integer isFollowed;
    private Integer isVoted;

    private String tag1;
    private String tag2;
    private String tag3;

    public Integer getIsVoted() { return isVoted; }
    public void setIsVoted(Integer isVoted) { this.isVoted = isVoted; }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public Integer getGoodCount() { return goodCount; }
    public void setGoodCount(Integer goodCount) { this.goodCount = goodCount; }
    public Integer getMiddleCount() { return middleCount; }
    public void setMiddleCount(Integer middleCount) { this.middleCount = middleCount; }
    public Integer getBadCount() { return badCount; }
    public void setBadCount(Integer badCount) { this.badCount = badCount; }
    public java.time.LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(java.time.LocalDateTime createTime) { this.createTime = createTime; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getIsFollowed() { return isFollowed; }
    public void setIsFollowed(Integer isFollowed) { this.isFollowed = isFollowed; }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }
}