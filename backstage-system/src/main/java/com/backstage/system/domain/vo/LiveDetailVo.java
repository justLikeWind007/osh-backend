package com.backstage.system.domain.vo;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/8
 * Time: 16:50
 */
public class LiveDetailVo {
    private Long id;
    private String title;
    private String cover;
    private String tryIntro;
    private BigDecimal price;
    private BigDecimal tPrice;
    private String type;
    private Integer subCount;
    private String content;
    private String isbuy = "false";
    private String isfava = "false";

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

    public String getTryIntro() {
        return tryIntro;
    }

    public void setTryIntro(String tryIntro) {
        this.tryIntro = tryIntro;
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

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsbuy() {
        return isbuy;
    }

    public void setIsbuy(String isbuy) {
        this.isbuy = isbuy;
    }

    public String getIsfava() {
        return isfava;
    }

    public void setIsfava(String isfava) {
        this.isfava = isfava;
    }

    @Override
    public String toString() {
        return "LiveDetailVo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", tryIntro='" + tryIntro + '\'' +
                ", price=" + price +
                ", tPrice=" + tPrice +
                ", type='" + type + '\'' +
                ", subCount=" + subCount +
                ", content='" + content + '\'' +
                ", isbuy=" + isbuy +
                ", isfava=" + isfava +
                '}';
    }
}
