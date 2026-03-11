package com.backstage.system.domain.group;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/3
 * Time: 20:38
 */
public class GroupActivity {
    /** 拼团活动ID */
    private Long id;
    /** 关联类型 (course-课程) */
    private String type;
    /** 商品ID (对应课程ID) */
    private Long goodsId;
    /** 拼团价格 */
    private String price;
    /** 拼团人数要求 */
    private Integer pNum;
    /** 拼团开始时间 */
    private LocalDateTime startTime;
    /** 拼团结束时间 */
    private LocalDateTime endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getpNum() {
        return pNum;
    }

    public void setpNum(Integer pNum) {
        this.pNum = pNum;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "GroupActivity{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", goodsId=" + goodsId +
                ", price='" + price + '\'' +
                ", pNum=" + pNum +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
