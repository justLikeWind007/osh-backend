package com.backstage.system.domain.dto.seckill;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 修改秒杀活动 DTO（只允许修改草稿状态的活动）
 * items 列表中：id 不为空表示修改已有明细，id 为空表示新增明细
 * 不在 items 中的已有明细将被逻辑删除
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "修改秒杀活动请求")
public class SeckillActivityUpdateDTO {

    @NotNull(message = "活动ID不能为空")
    @ApiModelProperty(value = "活动ID", required = true)
    private Long id;

    @ApiModelProperty("活动标题")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @Min(value = 1, message = "支付超时时间至少为1分钟")
    @ApiModelProperty("支付超时时间（分钟）")
    private Integer payTimeoutMin;

    @Valid
    @ApiModelProperty("活动商品明细列表（传入则全量替换，不传则不修改明细）")
    private List<SeckillActivityItemUpdateDTO> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Integer getPayTimeoutMin() { return payTimeoutMin; }
    public void setPayTimeoutMin(Integer payTimeoutMin) { this.payTimeoutMin = payTimeoutMin; }

    public List<SeckillActivityItemUpdateDTO> getItems() { return items; }
    public void setItems(List<SeckillActivityItemUpdateDTO> items) { this.items = items; }
}
