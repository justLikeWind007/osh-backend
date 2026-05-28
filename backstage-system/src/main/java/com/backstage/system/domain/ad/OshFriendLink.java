package com.backstage.system.domain.ad;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 友情链接/广告位
 */
@TableName("osh_friend_link")
public class OshFriendLink extends OSHBaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 排序序号（1-5） */
    private Integer sortOrder;

    /** 链接名称 */
    private String name;

    /** 链接地址 */
    private String url;

    /** 广告描述/简介 */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
