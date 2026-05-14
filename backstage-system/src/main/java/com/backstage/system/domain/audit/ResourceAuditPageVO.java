package com.backstage.system.domain.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "待审核资源分页结果")
public class ResourceAuditPageVO {

    @ApiModelProperty(value = "列表数据")
    private List<ResourceAuditItemVO> rows;

    @ApiModelProperty(value = "总数", example = "20")
    private Long total;

    @ApiModelProperty(value = "当前资源类型待审核总数", example = "20")
    private Long pendingTotal;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;

    @ApiModelProperty(value = "每页条数", example = "10")
    private Integer pageSize;

    public List<ResourceAuditItemVO> getRows() {
        return rows;
    }

    public void setRows(List<ResourceAuditItemVO> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPendingTotal() {
        return pendingTotal;
    }

    public void setPendingTotal(Long pendingTotal) {
        this.pendingTotal = pendingTotal;
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
}
