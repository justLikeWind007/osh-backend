package com.backstage.system.domain.seckill.es;

import com.backstage.system.jackson.FlexibleLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀商品明细 ES 文档实体
 * 对应索引 osh_seckill_item_search
 */
public class SeckillItemEsDocument {

    /** 明细ID，作为 ES 文档 ID */
    private Long id;

    /** 资源编号 */
    private String no;

    /** 关联活动ID */
    private Long activityId;

    /** 活动状态 */
    private Integer activityStatus;

    /** 商品ID */
    private Long goodsId;

    /** 商品类型：1-课程 2-书籍 3-商品 */
    private Integer goodsType;

    /** 商品标题 */
    private String title;

    /** 商品封面 */
    private String cover;

    /** 原价 */
    private BigDecimal originPrice;

    /** 秒杀价 */
    private BigDecimal seckillPrice;

    /** 总库存 */
    private Integer totalStock;

    /** 剩余库存 */
    private Integer availableStock;

    /** 已售数量 */
    private Integer soldCount;

    /** 每人限购 */
    private Integer limitPerUser;

    /** 排序 */
    private Integer sort;

    /** 活动标题 */
    private String activityTitle;

    /** 支付超时时间（分钟） */
    private Integer payTimeoutMin;

    /** 活动开始时间 */
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /** 活动结束时间 */
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /** 删除标记 */
    private Integer deleteFlag;

    /** 标签名称列表（keyword，用于精确过滤） */
    private List<String> tagNames;

    /** 标签名称文本（text，用于全文检索加权） */
    private String tagNamesText;

    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNo() { return no; }
    public void setNo(String no) { this.no = no; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Integer getActivityStatus() { return activityStatus; }
    public void setActivityStatus(Integer activityStatus) { this.activityStatus = activityStatus; }

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public Integer getGoodsType() { return goodsType; }
    public void setGoodsType(Integer goodsType) { this.goodsType = goodsType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }

    public BigDecimal getSeckillPrice() { return seckillPrice; }
    public void setSeckillPrice(BigDecimal seckillPrice) { this.seckillPrice = seckillPrice; }

    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }

    public Integer getLimitPerUser() { return limitPerUser; }
    public void setLimitPerUser(Integer limitPerUser) { this.limitPerUser = limitPerUser; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getActivityTitle() { return activityTitle; }
    public void setActivityTitle(String activityTitle) { this.activityTitle = activityTitle; }

    public Integer getPayTimeoutMin() { return payTimeoutMin; }
    public void setPayTimeoutMin(Integer payTimeoutMin) { this.payTimeoutMin = payTimeoutMin; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }

    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }

    public String getTagNamesText() { return tagNamesText; }
    public void setTagNamesText(String tagNamesText) { this.tagNamesText = tagNamesText; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
