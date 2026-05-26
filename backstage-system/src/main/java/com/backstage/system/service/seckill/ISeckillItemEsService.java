package com.backstage.system.service.seckill;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.vo.seckill.SeckillActivityUserVO;

/**
 * 秒杀商品明细 ES 搜索 Service 接口
 */
public interface ISeckillItemEsService {

    /**
     * 从 ES 搜索秒杀商品，结果按活动维度分组返回，与 MySQL 路径结构一致。
     * 分页粒度为活动（pageSize=10 表示返回 10 个活动）。
     *
     * @param keyword   商品名称关键词（可为 null）
     * @param goodsType 商品类型（可为 null）
     * @param pageNum   页码（从 1 开始）
     * @param pageSize  每页活动数量
     */
    PageResponse<SeckillActivityUserVO> searchActivities(
            String keyword, Integer goodsType, int pageNum, int pageSize);

    /**
     * 全量同步进行中活动的商品明细到 ES
     */
    int syncAllItemsToEs();
}
