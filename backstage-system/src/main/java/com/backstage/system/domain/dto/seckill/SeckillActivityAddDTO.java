package com.backstage.system.domain.dto.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 创建秒杀活动 DTO
 * 一次创建活动时可携带多个商品明细（items）
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "创建秒杀活动请求")
public class SeckillActivityAddDTO {

    @NotBlank(message = "活动标题不能为空")
    @ApiModelProperty(value = "活动标题", required = true)
    private String title;

    @NotNull(message = "活动开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "活动开始时间", required = true)
    private Date startTime;

    @NotNull(message = "活动结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "活动结束时间", required = true)
    private Date endTime;

    @NotNull(message = "支付超时时间不能为空")
    @Min(value = 1, message = "支付超时时间至少为1分钟")
    @ApiModelProperty(value = "支付超时时间（分钟），活动内所有商品共用", required = true)
    private Integer payTimeoutMin;

    @NotEmpty(message = "活动商品明细不能为空")
    @Valid
    @ApiModelProperty(value = "活动商品明细列表，至少包含一个商品", required = true)
    private List<SeckillActivityItemAddDTO> items;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Integer getPayTimeoutMin() { return payTimeoutMin; }
    public void setPayTimeoutMin(Integer payTimeoutMin) { this.payTimeoutMin = payTimeoutMin; }

    public List<SeckillActivityItemAddDTO> getItems() { return items; }
    public void setItems(List<SeckillActivityItemAddDTO> items) { this.items = items; }
}
