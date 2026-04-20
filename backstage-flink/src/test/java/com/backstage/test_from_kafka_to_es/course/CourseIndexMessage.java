package com.backstage.test_from_kafka_to_es.course;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CourseIndexMessage
{
    private Long courseId;
    private String title;
    private String intro;
    private String serviceContent;
    private String cover;
    private BigDecimal price;
    private BigDecimal tPrice;
    private String type;
    private Integer subCount;
    private String remark;
    private Integer totalDuration;
    private Integer freeLessonCount;
    private Integer videoCount;
    private Integer salesCount;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer questionCount;
    private Integer collectionCount;
    private BigDecimal ratingScore;
    private Integer freeType;
    private Integer afterServiceDays;
    private Integer resourceType;
    private Integer level;
    private Integer status;
    private Integer examId;
    private Integer deleteFlag;
    private List<String> tagNames;
    private String tagNamesText;
    private String searchText;
    private Date createTime;
    private Date updateTime;
    private String operator;

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getIntro()
    {
        return intro;
    }

    public void setIntro(String intro)
    {
        this.intro = intro;
    }

    public String getServiceContent()
    {
        return serviceContent;
    }

    public void setServiceContent(String serviceContent)
    {
        this.serviceContent = serviceContent;
    }

    public String getCover()
    {
        return cover;
    }

    public void setCover(String cover)
    {
        this.cover = cover;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getTPrice()
    {
        return tPrice;
    }

    public void setTPrice(BigDecimal tPrice)
    {
        this.tPrice = tPrice;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getSubCount()
    {
        return subCount;
    }

    public void setSubCount(Integer subCount)
    {
        this.subCount = subCount;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getTotalDuration()
    {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration)
    {
        this.totalDuration = totalDuration;
    }

    public Integer getFreeLessonCount()
    {
        return freeLessonCount;
    }

    public void setFreeLessonCount(Integer freeLessonCount)
    {
        this.freeLessonCount = freeLessonCount;
    }

    public Integer getVideoCount()
    {
        return videoCount;
    }

    public void setVideoCount(Integer videoCount)
    {
        this.videoCount = videoCount;
    }

    public Integer getSalesCount()
    {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount)
    {
        this.salesCount = salesCount;
    }

    public Long getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Long viewCount)
    {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount()
    {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount)
    {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount()
    {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount)
    {
        this.commentCount = commentCount;
    }

    public Integer getQuestionCount()
    {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount)
    {
        this.questionCount = questionCount;
    }

    public Integer getCollectionCount()
    {
        return collectionCount;
    }

    public void setCollectionCount(Integer collectionCount)
    {
        this.collectionCount = collectionCount;
    }

    public BigDecimal getRatingScore()
    {
        return ratingScore;
    }

    public void setRatingScore(BigDecimal ratingScore)
    {
        this.ratingScore = ratingScore;
    }

    public Integer getFreeType()
    {
        return freeType;
    }

    public void setFreeType(Integer freeType)
    {
        this.freeType = freeType;
    }

    public Integer getAfterServiceDays()
    {
        return afterServiceDays;
    }

    public void setAfterServiceDays(Integer afterServiceDays)
    {
        this.afterServiceDays = afterServiceDays;
    }

    public Integer getResourceType()
    {
        return resourceType;
    }

    public void setResourceType(Integer resourceType)
    {
        this.resourceType = resourceType;
    }

    public Integer getLevel()
    {
        return level;
    }

    public void setLevel(Integer level)
    {
        this.level = level;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getExamId()
    {
        return examId;
    }

    public void setExamId(Integer examId)
    {
        this.examId = examId;
    }

    public Integer getDeleteFlag()
    {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag)
    {
        this.deleteFlag = deleteFlag;
    }

    public List<String> getTagNames()
    {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames)
    {
        this.tagNames = tagNames;
    }

    public String getTagNamesText()
    {
        return tagNamesText;
    }

    public void setTagNamesText(String tagNamesText)
    {
        this.tagNamesText = tagNamesText;
    }

    public String getSearchText()
    {
        return searchText;
    }

    public void setSearchText(String searchText)
    {
        this.searchText = searchText;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }
}
