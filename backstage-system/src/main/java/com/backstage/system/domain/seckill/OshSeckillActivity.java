package com.backstage.system.domain.seckill;

import com.backstage.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 秒杀活动对象 osh_seckill_activity
 * 活动只保留活动级别字段（标题、时间、状态、支付超时）
 * 商品相关字段（库存、价格、限购等）已移至 osh_seckill_activity_item
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "秒杀活动")
public class OshSeckillActivity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @Excel(name = "活动标题")
    @ApiModelProperty("活动标题")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "开始时间")
    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "结束时间")
    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @Excel(name = "活动状态")
    @ApiModelProperty("活动状态：0-草稿 1-未开始 2-进行中 3-已结束 4-已下架")
    private Integer status;

    @Excel(name = "支付超时(分钟)")
    @ApiModelProperty("支付超时时间（分钟），活动内所有商品共用同一支付超时配置")
    private Integer payTimeoutMin;

    @ApiModelProperty("创建人")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标记：0-正常 1-已删除")
    private Integer deleteFlag;

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

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }
}
