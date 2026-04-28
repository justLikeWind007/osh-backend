package com.backstage.system.domain.vo.book;

import java.io.Serializable;

/**
 * 电子书关联状态响应 VO
 */
public class BookRelationStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否收藏（0-否，1-是） */
    private Integer favorited;

    /** 是否关注（0-否，1-是） */
    private Integer followed;

    /** 是否购买（0-否，1-是） */
    private Integer purchased;

    public Integer getFavorited() { return favorited; }
    public void setFavorited(Integer favorited) { this.favorited = favorited; }

    public Integer getFollowed() { return followed; }
    public void setFollowed(Integer followed) { this.followed = followed; }

    public Integer getPurchased() { return purchased; }
    public void setPurchased(Integer purchased) { this.purchased = purchased; }
}
