package com.backstage.system.domain.assistant.dto;

/**
 * AI 助手反馈分页查询 DTO
 *
 * @author backstage
 */
public class AssistantFeedbackPageDTO {

    /**
     * 分类 ID（可选）
     */
    private Long categoryId;

    /**
     * 分类代码（可选）
     */
    private String categoryCode;

    /**
     * 状态（可选）
     */
    private String status;

    /**
     * 是否置顶（可选，0-否 1-是）
     */
    private Integer isPinned;

    /**
     * 关键词搜索（标题或内容）
     */
    private String keyword;

    /**
     * 用户 ID（查询指定用户的反馈）
     */
    private Long userId;

    /**
     * 是否仅查询公告（可选，0-否 1-是）
     */
    private Integer isAnnouncement;

    /**
     * 排序类型（可选：hot-最热，latest-最新，comment-最多评论）
     */
    private String sortType;

    /**
     * 页码（默认 1）
     */
    private Integer pageNum = 1;

    /**
     * 每页数量（默认 10）
     */
    private Integer pageSize = 10;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Integer isPinned) {
        this.isPinned = isPinned;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getIsAnnouncement() {
        return isAnnouncement;
    }

    public void setIsAnnouncement(Integer isAnnouncement) {
        this.isAnnouncement = isAnnouncement;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
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
