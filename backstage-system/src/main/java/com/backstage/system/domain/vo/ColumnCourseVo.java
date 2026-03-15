package com.backstage.system.domain.vo;

import java.math.BigDecimal;

/**
 * 专栏课程信息
 */
public class ColumnCourseVo
{
    /**
     * 课程id
     */
    private Long id;

    /**
     * 课程名
     */
    private String title;

    /**
     * 课程价格
     */
    private BigDecimal price;

    /**
     * 课程类型 eg: media
     */
    private String type;

    public ColumnCourseVo() {
    }

    public ColumnCourseVo(Long id, String title, BigDecimal price, String type) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
