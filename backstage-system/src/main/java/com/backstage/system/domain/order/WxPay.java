package com.backstage.system.domain.order;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;

/**
 * 微信支付对象 osh_wxpay
 * 
 * @author ruoyi
 * @date 2026-03-08
 */
public class WxPay extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private String no;

    /**  */
    @Excel(name = "")
    private String price;

    /**  */
    @Excel(name = "")
    private String codeUrl;

    public void setNo(String no)
    {
        this.no = no;
    }

    public String getNo()
    {
        return no;
    }

    public void setPrice(String price) 
    {
        this.price = price;
    }

    public String getPrice() 
    {
        return price;
    }

    public void setCodeUrl(String codeUrl) 
    {
        this.codeUrl = codeUrl;
    }

    public String getCodeUrl() 
    {
        return codeUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getNo())
            .append("price", getPrice())
            .append("codeUrl", getCodeUrl())
            .toString();
    }
}
