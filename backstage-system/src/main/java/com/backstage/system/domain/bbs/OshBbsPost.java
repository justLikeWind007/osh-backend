package com.backstage.system.domain.bbs;

import com.backstage.common.core.domain.BaseEntity;

/**
 * 帖子对象 osh_bbs_post
 */
public class OshBbsPost extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;        // 作者ID
    private Long categoryId;    // 分类ID (新加)
    private String title;       // 标题
    private String content;     // 正文
    private String images;      // 图片 (新加)
    private Integer viewCount;  // 浏览量
    private Integer supportCount; // 点赞数
    private Integer commentCount; // 评论数
    private Integer isTop;      // 是否置顶
    private Integer isDelete;   // 是否删除 (新加)

    // Getter 和 Setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getSupportCount() { return supportCount; }
    public void setSupportCount(Integer supportCount) { this.supportCount = supportCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getIsTop() { return isTop; }
    public void setIsTop(Integer isTop) { this.isTop = isTop; }
    public Integer getIsDelete() { return isDelete; }
    public void setIsDelete(Integer isDelete) { this.isDelete = isDelete; }
}