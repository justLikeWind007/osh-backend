package com.backstage.system.domain;

import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    /** 商品id */
    @Excel(name = "商品id")
    private Long goodsId;

    /** 秒杀价格 */
    @Excel(name = "秒杀价格")
    private String flashPrice;

    /** 数量 */
    @Excel(name = "数量")
    private String sNum;

    /** 使用数量 */
    @Excel(name = "使用数量")
    private String usedNum;

    /** 开始时间 */
    @Excel(name = "开始时间")
    private String startTime;

    /** 结束时间 */
    @Excel(name = "结束时间")
    private String endTime;

    /** 秒杀类型 */
    @Excel(name = "秒杀类型")
    private String flashType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setGoodsId(Long goodsId)
    {
        this.goodsId = goodsId;
    }

    public Long getGoodsId()
    {
        return goodsId;
    }

    public void setFlashPrice(String flashPrice)
    {
        this.flashPrice = flashPrice;
    }

    public String getFlashPrice()
    {
        return flashPrice;
    }

    public void setsNum(String sNum)
    {
        this.sNum = sNum;
    }

    public String getsNum()
    {
        return sNum;
    }

    public void setUsedNum(String usedNum)
    {
        this.usedNum = usedNum;
    }

    public String getUsedNum()
    {
        return usedNum;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setFlashType(String flashType)
    {
        this.flashType = flashType;
    }

    public String getFlashType()
    {
        return flashType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("goodsId", getGoodsId())
                .append("flashPrice", getFlashPrice())
                .append("sNum", getsNum())
                .append("usedNum", getUsedNum())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("flashType", getFlashType())
                .toString();
    }
}
