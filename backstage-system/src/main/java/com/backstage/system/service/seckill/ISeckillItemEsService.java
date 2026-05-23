package com.backstage.system.service.seckill;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.vo.seckill.SeckillActivityItemVO;

/**
 * 秒杀商品明细 ES 搜索 Service 接口
 */
public interface ISeckillItemEsService {

    /**
     * 搜索秒杀商品明细
     *
     * @param keyword   商品名称关键词（可为 null）
     * @param goodsType 商品类型（可为 null）
     * @param pageNum   页码
     * @param pageSize  每页大小
     */
    PageResponse<SeckillActivityItemVO> searchItems(
            String keyword, Integer goodsType, int pageNum, int pageSize);

    /**
     * 全量同步进行中活动的商品明细到 ES
     */
    int syncAllItemsToEs();
}
