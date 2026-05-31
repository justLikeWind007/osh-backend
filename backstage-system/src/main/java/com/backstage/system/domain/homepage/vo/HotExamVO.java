package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 首页热门考试 VO
 *
 * @author jayTatum
 */
@ApiModel(description = "首页热门考试")
public class HotExamVO {

    @ApiModelProperty("考试ID")
    private Long id;

    @ApiModelProperty("考试标题")
    private String title;

    @ApiModelProperty("封面URL")
    private String cover;

    @ApiModelProperty("考试描述")
    private String description;

    @ApiModelProperty("总分")
    private Integer totalScore;

    @ApiModelProperty("及格分")
    private Integer passScore;

    @ApiModelProperty("考试时长（分钟）")
    private Integer expire;

    @ApiModelProperty("题目数量")
    private Integer questionCount;

    @ApiModelProperty("收藏数")
    private Integer collectCount;

    @ApiModelProperty("热度分")
    private Double hotScore;

    @ApiModelProperty("标签列表（最多2个）")
    private List<String> tags;

    @ApiModelProperty("详情页跳转路径")
    private String detailUrl;

    // 标签查询用临时字段
    @ApiModelProperty(hidden = true)
    private Long examId;

    @ApiModelProperty(hidden = true)
    private String tagName;

    // ========== getter / setter ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public Integer getPassScore() { return passScore; }
    public void setPassScore(Integer passScore) { this.passScore = passScore; }

    public Integer getExpire() { return expire; }
    public void setExpire(Integer expire) { this.expire = expire; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public Integer getCollectCount() { return collectCount; }
    public void setCollectCount(Integer collectCount) { this.collectCount = collectCount; }

    public Double getHotScore() { return hotScore; }
    public void setHotScore(Double hotScore) { this.hotScore = hotScore; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getDetailUrl() { return detailUrl; }
    public void setDetailUrl(String detailUrl) { this.detailUrl = detailUrl; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }
}
