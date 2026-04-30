package com.backstage.system.domain.vo.search;

import java.io.Serializable;
import java.math.BigDecimal;

public class SearchResultVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String cover;
    private BigDecimal t_price;
    private String type;
    private Integer isTry; // 对应数据库的 try 字段

    public Integer getIsTry() {
        return isTry;
    }

    public void setIsTry(Integer isTry) {
        this.isTry = isTry;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public BigDecimal getT_price() {
        return t_price;
    }

    public void setT_price(BigDecimal t_price) {
        this.t_price = t_price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




    @Override
    public String toString() {
        return "SearchResultVo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", t_price=" + t_price +
                ", type='" + type + '\'' +
                ", try=" + isTry +
                '}';
    }
}