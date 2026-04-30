package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 评价信息 DTO
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "评价信息")
public class ReviewDTO {
    
    /** 课程 ID */
    @ApiModelProperty("课程 ID")
    private Long courseId;
    
    /** 评分：1-差评，2-中评，3-好评 */
    @ApiModelProperty("评分：1-差评，2-中评，3-好评")
    private Integer rating;
    
    /** 评价内容 */
    @ApiModelProperty("评价内容")
    private String reviewContent;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }
}
