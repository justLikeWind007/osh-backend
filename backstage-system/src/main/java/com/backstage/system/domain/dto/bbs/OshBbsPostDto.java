package com.backstage.system.domain.dto.bbs;

import com.backstage.system.domain.bbs.OshBbsPost;

/**
 * 帖子业务传输对象：继承自基础实体，扩展用户信息
 */
public class OshBbsPostDto extends OshBbsPost {
    private String nickName;
    private String avatar;
    private String sex;

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
}