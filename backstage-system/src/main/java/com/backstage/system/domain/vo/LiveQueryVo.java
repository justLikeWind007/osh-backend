package com.backstage.system.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/8
 * Time: 16:50
 */
public class LiveQueryVo {
    private Long id;
    private String title;
    private String cover;
    private String tryIntro;      // 对应接口中的try
    private String key;
    private BigDecimal price;
    private BigDecimal tPrice;
    private Long schoolId;
    private Integer subCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
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
        return "LiveQueryVo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", tryIntro='" + tryIntro + '\'' +
                ", key='" + key + '\'' +
                ", price=" + price +
                ", tPrice=" + tPrice +
                ", schoolId=" + schoolId +
                ", subCount=" + subCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }
}
