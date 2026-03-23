package com.backstage.system.domain.vo;

import com.alibaba.fastjson2.annotation.JSONField;

import java.math.BigDecimal;
import java.util.List;

/**
 * 专栏信息
 */
public class ColumnDetailVo {
    /**
     * 专栏id
     */
    private Long id;

    /**
     * 专栏标题
     */
    private String title;

    /**
     * 专栏封面
     */
    private String cover;

    /**
     * 专栏描述
     */
    private String colTry;

    /**
     * 专栏内容
     */
    private String content;

    /**
     * 现价
     */
    private BigDecimal price;

    /**
     * 原价
     */
    @JSONField(name = "t_price")
    private BigDecimal tPrice;

    /**
     * 专栏是否完结: 0:未结束 1:已结束
     */
    private Integer isEnd;

    /**
     * 订阅数
     */
    @JSONField(name = "sub_count")
    private Integer subCount;

    private List<ColumnCourseVo> columnCoursVos;

    /**
     * 是否已订阅: 0:未订阅 1:已订阅
     */
    private Boolean buyFlag;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Integer getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(Integer isEnd) {
        this.isEnd = isEnd;
    }

    public Integer getSubCount() {
        return subCount;
    }

    public void setSubCount(Integer subCount) {
        this.subCount = subCount;
    }

    public List<ColumnCourseVo> getColumnCourses() {
        return columnCoursVos;
    }

    public void setColumnCourses(List<ColumnCourseVo> columnCoursVos) {
        this.columnCoursVos = columnCoursVos;
    }

    public Boolean getBuyFlag() {
        return buyFlag;
    }

    public void setBuyFlag(Boolean buyFlag) {
        this.buyFlag = buyFlag;
    }

    public List<ColumnCourseVo> getColumnCoursVos() {
        return columnCoursVos;
    }

    public void setColumnCoursVos(List<ColumnCourseVo> columnCoursVos) {
        this.columnCoursVos = columnCoursVos;
    }

    public ColumnDetailVo() {
    }

    public ColumnDetailVo(Long id, String title, String cover, String colTry, String content, BigDecimal price, BigDecimal tPrice, Integer isEnd, Integer subCount, List<ColumnCourseVo> columnCoursVos, Boolean buyFlag) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.colTry = colTry;
        this.content = content;
        this.price = price;
        this.tPrice = tPrice;
        this.isEnd = isEnd;
        this.subCount = subCount;
        this.columnCoursVos = columnCoursVos;
        this.buyFlag = buyFlag;
    }
}
