package com.backstage.system.domain.course.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class OshCourseDetailVo {

    private Long id;

    private String title;

    private String cover;

    private String intro;

    private String serviceContent;

    private BigDecimal price;

    private BigDecimal tPrice;

    private Integer subCount;

    private Integer buyCount;

    private String remark;

    private Integer totalDuration;

    private Integer videoCount;

    private Integer freeLessonCount;

    private Long viewCount;

    private Integer goodCount;

    private Integer midCount;

    private Integer badCount;

    private Integer commentCount;

    private BigDecimal ratingScore;

    private Integer freeType;

    private Integer afterServiceDays;

    private Integer status;

    private Integer examId;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private List<OshCourseTagSimpleVo> tags;

    private Integer buyFlag;

    public OshCourseDetailVo() {
    }

    public OshCourseDetailVo(Long id, String title, String cover, String intro, String serviceContent, BigDecimal price, BigDecimal tPrice, Integer subCount, String remark, Integer totalDuration, Integer videoCount, Integer freeLessonCount, Long viewCount, Integer commentCount, BigDecimal ratingScore, Integer freeType, Integer afterServiceDays, Integer status, Integer examId, String createBy, Date createTime, String updateBy, Date updateTime, List<OshCourseTagSimpleVo> tags, Integer buyFlag) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.intro = intro;
        this.serviceContent = serviceContent;
        this.price = price;
        this.tPrice = tPrice;
        this.subCount = subCount;
        this.remark = remark;
        this.totalDuration = totalDuration;
        this.videoCount = videoCount;
        this.freeLessonCount = freeLessonCount;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
        this.ratingScore = ratingScore;
        this.freeType = freeType;
        this.afterServiceDays = afterServiceDays;
        this.status = status;
        this.examId = examId;
        this.createBy = createBy;
        this.createTime = createTime;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
        this.tags = tags;
        this.buyFlag = buyFlag;
    }

    public BigDecimal gettPrice() {
        return tPrice;
    }

    public void settPrice(BigDecimal tPrice) {
        this.tPrice = tPrice;
    }

    public Integer getBuyFlag() {
        return buyFlag;
    }

    public void setBuyFlag(Integer buyFlag) {
        this.buyFlag = buyFlag;
    }

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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getServiceContent() {
        return serviceContent;
    }

    public void setServiceContent(String serviceContent) {
        this.serviceContent = serviceContent;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTPrice() {
        return tPrice;
    }

    public void setTPrice(BigDecimal tPrice) {
        this.tPrice = tPrice;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
    }

    public Integer getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Integer buyCount) {
        this.buyCount = buyCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Integer getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(Integer videoCount) {
        this.videoCount = videoCount;
    }

    public Integer getFreeLessonCount() {
        return freeLessonCount;
    }

    public void setFreeLessonCount(Integer freeLessonCount) {
        this.freeLessonCount = freeLessonCount;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(Integer goodCount) {
        this.goodCount = goodCount;
    }

    public Integer getMidCount() {
        return midCount;
    }

    public void setMidCount(Integer midCount) {
        this.midCount = midCount;
    }

    public Integer getBadCount() {
        return badCount;
    }

    public void setBadCount(Integer badCount) {
        this.badCount = badCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public BigDecimal getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(BigDecimal ratingScore) {
        this.ratingScore = ratingScore;
    }

    public Integer getFreeType() {
        return freeType;
    }

    public void setFreeType(Integer freeType) {
        this.freeType = freeType;
    }

    public Integer getAfterServiceDays() {
        return afterServiceDays;
    }

    public void setAfterServiceDays(Integer afterServiceDays) {
        this.afterServiceDays = afterServiceDays;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<OshCourseTagSimpleVo> getTags() {
        return tags;
    }

    public void setTags(List<OshCourseTagSimpleVo> tags) {
        this.tags = tags;
    }
}
