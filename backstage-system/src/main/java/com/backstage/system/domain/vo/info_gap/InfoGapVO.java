package com.backstage.system.domain.vo.info_gap;

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
    private java.time.LocalDateTime createTime;
    
    // 发布者信息 (从 OshUser 表关联)
    private String nickname;
    private String avatar;
    
    // 扩展功能：当前登录用户是否已关注该作者
    private Boolean isFollowed;

    private Integer isVoted;

    public Integer getIsVoted() { return isVoted; }
    public void setIsVoted(Integer isVoted) { this.isVoted = isVoted; }

    // Getter and Setter
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
    public Boolean getIsFollowed() { return isFollowed; }
    public void setIsFollowed(Boolean isFollowed) { this.isFollowed = isFollowed; }
}