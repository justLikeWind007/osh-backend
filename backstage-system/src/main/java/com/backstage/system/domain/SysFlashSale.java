package com.backstage.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 秒杀活动对象 osh_flashsale
 *
 * @author 星号
 * @date 2026-03-04
 */
public class SysFlashSale extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 秒杀活动ID (JSON里的flashsale_id) */
    @Excel(name = "秒杀活动ID")
    private Long flashsaleId;

    /** 商品id (JSON里的id) */
    @Excel(name = "商品id")
    private Long goodsId;

    /** 标题 */
    @Excel(name = "标题")
    private String title;

    /** 封面图 */
    @Excel(name = "封面图")
    private String cover;

    /** 秒杀价格 */
    @Excel(name = "秒杀价格")
    private BigDecimal flashPrice;

    /** 原价 */
    @Excel(name = "原价")
    private BigDecimal tPrice;

    /** 数量 */
    @Excel(name = "数量")
    private Integer sNum;

    /** 使用数量 */
    @Excel(name = "使用数量")
    private Integer usedNum;

    /** 开始时间 */
    @Excel(name = "开始时间")
    private String startTime; // 改回 String

    /** 结束时间 */
    @Excel(name = "结束时间")
    private String endTime; // 改回 String

    /** 秒杀类型 */
    @Excel(name = "秒杀类型")
    private String flashType;


    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setFlashsaleId(Long flashsaleId) { this.flashsaleId = flashsaleId; }
    public Long getFlashsaleId() { return flashsaleId; }

    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public Long getGoodsId() { return goodsId; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setCover(String cover) { this.cover = cover; }
    public String getCover() { return cover; }

    public void setFlashPrice(BigDecimal flashPrice) { this.flashPrice = flashPrice; }
    public BigDecimal getFlashPrice() { return flashPrice; }

    public void settPrice(BigDecimal tPrice) { this.tPrice = tPrice; }
    public BigDecimal gettPrice() { return tPrice; }

    public void setsNum(Integer sNum) { this.sNum = sNum; }
    public Integer getsNum() { return sNum; }

    public void setUsedNum(Integer usedNum) { this.usedNum = usedNum; }
    public Integer getUsedNum() { return usedNum; }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public void setFlashType(String flashType) { this.flashType = flashType; }
    public String getFlashType() { return flashType; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("flashsaleId", getFlashsaleId())
                .append("goodsId", getGoodsId())
                .append("title", getTitle())
                .append("cover", getCover())
                .append("flashPrice", getFlashPrice())
                .append("tPrice", gettPrice())
                .append("sNum", getsNum())
                .append("usedNum", getUsedNum())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("flashType", getFlashType())
                .toString();
    }
}