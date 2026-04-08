package com.backstage.system.controller.book;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class BookListReqVO {

    @ApiModelProperty(value = "当前页码")
    private Long pageNum;

    @ApiModelProperty(value = "每页数量")
    private Long pageSize;

    @ApiModelProperty(value = "电子书标题")
    private String title;

    /**
     * 标签名列表
     */
    private List<String> tagNameList;

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getTagNameList() {
        return tagNameList;
    }

    public void setTagNameList(List<String> tagNameList) {
        this.tagNameList = tagNameList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
