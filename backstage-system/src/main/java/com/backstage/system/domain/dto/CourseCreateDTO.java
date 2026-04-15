package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

/**
 * 课程创建DTO（包含章节信息）
 *
 * @author ruoyi
 * @date 2026-03-27
 */
@ApiModel(description = "课程创建DTO")
public class CourseCreateDTO {

    @ApiModelProperty("课程标题")
    @NotBlank(message = "课程标题不能为空")
    private String title;

    @ApiModelProperty("课程封面图URL")
    private String cover;

    @ApiModelProperty("课程类型（media-视频课 live-直播课 text-图文课）")
    private String type;

    @ApiModelProperty("课程状态：0-草稿 1-已发布")
    private Integer status;

    @ApiModelProperty("当前售价")
    private BigDecimal price;

    @ApiModelProperty("原价/市场价")
    private BigDecimal tPrice;

    @ApiModelProperty("课程介绍")
    private String intro;

    @ApiModelProperty("课程人工服务周期（月）")
    private Integer servicePeriod;

    @ApiModelProperty("具体包含服务")
    private String serviceContent;

    @ApiModelProperty("免费试看章节数量")
    private Integer freeLessonCount;

    @ApiModelProperty("所属专栏ID")
    private Long columnId;

    @ApiModelProperty("关联考试ID")
    private Long examId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("标签ID列表")
    private List<Long> tagIds;

    @ApiModelProperty("章节列表")
    private List<SectionCreateDTO> sections;

    // ==================== Getter/Setter ====================

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Integer getServicePeriod() {
        return servicePeriod;
    }

    public void setServicePeriod(Integer servicePeriod) {
        this.servicePeriod = servicePeriod;
    }

    public String getServiceContent() {
        return serviceContent;
    }

    public void setServiceContent(String serviceContent) {
        this.serviceContent = serviceContent;
    }

    public Integer getFreeLessonCount() {
        return freeLessonCount;
    }

    public void setFreeLessonCount(Integer freeLessonCount) {
        this.freeLessonCount = freeLessonCount;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public List<SectionCreateDTO> getSections() {
        return sections;
    }

    public void setSections(List<SectionCreateDTO> sections) {
        this.sections = sections;
    }
}