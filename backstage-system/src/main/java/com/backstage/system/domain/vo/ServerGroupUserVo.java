package com.backstage.system.domain.vo;

import java.time.LocalDateTime;

/**
 * 服务器拼团参团用户VO
 * 
 * @author system
 * @date 2026-04-18
 */
public class ServerGroupUserVo {
    
    /** 用户ID */
    private Long userId;
    
    /** 用户名 */
    private String username;
    
    /** 用户昵称 */
    private String nickname;
    
    /** 用户头像 */
    private String avatar;
    
    /** 是否为团长 */
    private Boolean isLeader;
    
    /** 参团时间 */
    private LocalDateTime joinTime;
    
    // Getter and Setter
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public Boolean getIsLeader() {
        return isLeader;
    }
    
    public void setIsLeader(Boolean isLeader) {
        this.isLeader = isLeader;
    }
    
    public LocalDateTime getJoinTime() {
        return joinTime;
    }
    
    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }
}
