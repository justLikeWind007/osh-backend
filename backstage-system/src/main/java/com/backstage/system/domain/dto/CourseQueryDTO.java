package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 课程查询条件 DTO
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "课程查询条件")
public class CourseQueryDTO {
    
    /** 页码 */
    @ApiModelProperty("页码")
    private Integer pageNum = 1;
    
    /** 每页数量 */
    @ApiModelProperty("每页数量")
    private Integer pageSize = 10;
    
    /** 标签 ID 列表 */
    @ApiModelProperty("标签 ID 列表")
    private List<Long> tagIds;
    
    /** 关键字 */
    @ApiModelProperty("搜索关键字")
    private String keyword;
    
    /** 排序字段 */
    @ApiModelProperty("排序字段：create_time|price|usage_count")
    private String sortBy = "create_time";
    
    /** 排序方式 */
    @ApiModelProperty("排序方式：asc|desc")
    private String sortOrder = "desc";

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
