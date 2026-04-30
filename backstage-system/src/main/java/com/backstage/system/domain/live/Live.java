package com.backstage.system.domain.live;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/8
 * Time: 16:47
 */
public class Live {
    private Long id;                 // 直播ID
    private String title;             // 直播标题
    private String cover;             // 封面图
    private String tryIntro;          // 简介
    private String content;           // 详细介绍
    private String key;           // 直播密钥（对应key字段）
    private String type;              // 直播类型
    private BigDecimal price;         // 当前价格
    private BigDecimal tPrice;        // 原价（对应t_price）
    private Integer subCount;         // 订阅人数（对应sub_count）
    private Long schoolId;            // 网校ID
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;    // 结束时间
    private LocalDateTime createdTime;// 创建时间
    private LocalDateTime updatedTime;// 更新时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTryIntro() {
        return tryIntro;
    }

    public void setTryIntro(String tryIntro) {
        this.tryIntro = tryIntro;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal gettPrice() {
        return tPrice;
    }

    public void settPrice(BigDecimal tPrice) {
        this.tPrice = tPrice;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "Live{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", tryIntro='" + tryIntro + '\'' +
                ", content='" + content + '\'' +
                ", key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", tPrice=" + tPrice +
                ", subCount=" + subCount +
                ", schoolId=" + schoolId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
