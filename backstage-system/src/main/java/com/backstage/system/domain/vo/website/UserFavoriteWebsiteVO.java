package com.backstage.system.domain.vo.website;

import java.time.LocalDateTime;

/**
 * 用户收藏网站 VO类
 * 用于前端展示收藏的网站信息
 */
public class UserFavoriteWebsiteVO {

    // 网站名称
    private String websiteName;

    // 网站地址
    private String websiteUrl;

    // 网站描述
    private String websiteDescription;

    // 收藏时间
    private LocalDateTime favoriteTime;

    // 收藏备注
    private String remark;

    // 无参构造函数（必须）
    public UserFavoriteWebsiteVO() {
    }

    // 全参构造函数（可选，方便赋值）
    public UserFavoriteWebsiteVO(String websiteName, String websiteUrl, String websiteDescription, LocalDateTime favoriteTime, String remark) {
        this.websiteName = websiteName;
        this.websiteUrl = websiteUrl;
        this.websiteDescription = websiteDescription;
        this.favoriteTime = favoriteTime;
        this.remark = remark;
    }

    // Getter & Setter 方法
    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getWebsiteDescription() {
        return websiteDescription;
    }

    public void setWebsiteDescription(String websiteDescription) {
        this.websiteDescription = websiteDescription;
    }

    public LocalDateTime getFavoriteTime() {
        return favoriteTime;
    }

    public void setFavoriteTime(LocalDateTime favoriteTime) {
        this.favoriteTime = favoriteTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    // toString 方法（打印对象时查看字段值）
    @Override
    public String toString() {
        return "UserFavoriteWebsiteVo{" +
                "websiteName='" + websiteName + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", websiteDescription='" + websiteDescription + '\'' +
                ", favoriteTime=" + favoriteTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}
