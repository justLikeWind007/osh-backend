package com.backstage.system.domain.dto.exam;

import java.io.Serializable;

/**
 * 考试搜索/列表查询 DTO
 */
public class ExamSearchDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String keyword;
    private String tag;
    private String resourceType;
    private Long resourceId;
    /** 1=只看收藏 */
    private Integer collectFlag;
    private Integer pageNum;
    private Integer pageSize;
    /** 当前用户ID（内部使用，不由前端传入） */
    private Long userId;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
    public Integer getCollectFlag() { return collectFlag; }
    public void setCollectFlag(Integer collectFlag) { this.collectFlag = collectFlag; }
    public Integer getPageNum() { return pageNum != null ? pageNum : 1; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize != null ? pageSize : 12; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
