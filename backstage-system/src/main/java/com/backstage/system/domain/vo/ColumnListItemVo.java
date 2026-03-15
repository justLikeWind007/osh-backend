package com.backstage.system.domain.vo;

import com.alibaba.fastjson2.annotation.JSONField;

import java.math.BigDecimal;

/**
 * 专栏列表项
 */
public class ColumnListItemVo {
    private Long id;

    private String title;

    private String cover;

    @JSONField(name = "try")
    private String colTry;

    private BigDecimal price;

    @JSONField(name = "t_price")
    private BigDecimal tPrice;

    private String type;

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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getColTry() {
        return colTry;
    }

    public void setColTry(String colTry) {
        this.colTry = colTry;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal gettPrice() {
        return tPrice;
    }

    public void settPrice(BigDecimal tPrice) {
        this.tPrice = tPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
