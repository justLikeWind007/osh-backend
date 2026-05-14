package com.backstage.system.domain.dto.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量修改秒杀商品状态 DTO（上架/下架）
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "批量修改秒杀商品状态请求")
public class SeckillGoodsStatusDTO {

    @NotEmpty(message = "商品ID列表不能为空")
    @ApiModelProperty(value = "商品池主键ID列表，支持批量", required = true)
    private List<Long> ids;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty(value = "目标状态：1-上架 2-下架", required = true)
    private Integer status;

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
