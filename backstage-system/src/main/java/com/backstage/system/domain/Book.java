package com.backstage.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 电子书对象 book
 *
 * @author backstage
 */
@Data
@TableName("osh_book")
public class Book
{
    private static final long serialVersionUID = 1L;

    /** 电子书ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 封面 */
    private String cover;

    /** 描述 */
    @JsonProperty("desc")
    private String description;

    /** 试读内容 */
    @JsonProperty("try")
    private String tryContent;

    /** 价格 */
    private BigDecimal price;

    /** 原价 */
    @JsonProperty("t_price")
    private BigDecimal originalPrice;

    /** 订阅数 */
    @JsonProperty("sub_count")
    private Integer subCount;

    /** 状态（0正常 1下架） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;
}
