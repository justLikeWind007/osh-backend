package com.backstage.system.service.seckill;

import com.alibaba.fastjson2.annotation.JSONField;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品明细索引 upsert 消息
 * 包含 ES 文档所需的全部字段，由 OshSeckillActivityServiceImpl 构建后写入 outbox 表
 */
public class SeckillItemIndexUpsertMessage {

    private String eventType;

    /** 明细ID，作为 ES 文档 ID */
    private Long id;

    /** 关联活动ID */
    private Long activityId;

    /** 活动状态（冗余），Flink 用此字段决定 upsert 还是 delete */
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

    /** 活动开始时间 */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date startTime;

    /** 活动结束时间 */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date endTime;

    /** 删除标记 */
    private Integer deleteFlag;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date createTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date updateTime;

    /** 操作人，不序列化到消息体 */
    @JSONField(serialize = false)
    private String operator;

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
}
