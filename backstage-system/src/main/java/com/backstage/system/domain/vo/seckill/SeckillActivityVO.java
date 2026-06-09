package com.backstage.system.domain.vo.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 秒杀活动响应 VO（管理端）
 * 活动本身只含活动级别字段，商品明细通过 items 列表返回
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "秒杀活动响应")
public class SeckillActivityVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动ID")
    private Long id;

    @ApiModelProperty("活动标题")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动状态：0-草稿 1-未开始 2-进行中 3-已结束 4-已下架")
    private Integer status;

    @ApiModelProperty("支付超时时间（分钟）")
    private Integer payTimeoutMin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("活动商品明细列表")
    private List<SeckillActivityItemVO> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getPayTimeoutMin() { return payTimeoutMin; }
    public void setPayTimeoutMin(Integer payTimeoutMin) { this.payTimeoutMin = payTimeoutMin; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public List<SeckillActivityItemVO> getItems() { return items; }
    public void setItems(List<SeckillActivityItemVO> items) { this.items = items; }
}
