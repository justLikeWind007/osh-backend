package com.backstage.system.domain.dto.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量发布/下架秒杀活动 DTO
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "批量发布/下架秒杀活动请求")
public class SeckillActivityStatusDTO {

    @NotEmpty(message = "活动ID列表不能为空")
    @ApiModelProperty(value = "活动ID列表，支持批量", required = true)
    private List<Long> ids;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty(value = "目标状态：1-发布(未开始) 4-下架", required = true)
    private Integer status;

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
