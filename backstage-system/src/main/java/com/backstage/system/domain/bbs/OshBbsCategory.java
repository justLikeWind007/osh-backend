package com.backstage.system.domain.bbs;

import com.backstage.common.core.domain.BaseEntity;

/**
 * 社区分类实体类 osh_bbs_category
 */
public class OshBbsCategory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;    // 分类标题
    private String image;    // 分类图片/图标
    private Integer sort;    // 排序
    private Integer isDelete; // 是否删除 0否 1是

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getIsDelete() { return isDelete; }
    public void setIsDelete(Integer isDelete) { this.isDelete = isDelete; }
}