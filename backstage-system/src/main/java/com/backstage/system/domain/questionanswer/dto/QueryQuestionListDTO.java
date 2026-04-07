package com.backstage.system.domain.questionanswer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/29
 * Time: 18:08
 */
@ApiModel(description = "查询问题列表实体类")
public class QueryQuestionListDTO {
    @ApiModelProperty(
            value = "资源编号"
    )
    private Long resourceNo;
    @ApiModelProperty(
            value = "资源类型"
    )
    private String resourceType;
    @ApiModelProperty(
            value = "搜索类型",
            required = true
    )
    private String type;
    @ApiModelProperty(
            value = "搜索关键字"
    )
    private String keyword;
    @ApiModelProperty(
            value = "页码",
            required = true
    )
    private Integer pageNum;
    @ApiModelProperty(
            value = "每页数量",
            required = true
    )
    private Integer pageSize;

    public Long getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(Long resourceNo) {
        this.resourceNo = resourceNo;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

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

    @Override
    public String toString() {
        return "QueryQuestionListDTO{" +
                "resourceNo='" + resourceNo + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", type='" + type + '\'' +
                ", keyword='" + keyword + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
