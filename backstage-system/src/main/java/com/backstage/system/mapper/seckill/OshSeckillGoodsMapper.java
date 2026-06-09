package com.backstage.system.mapper.seckill;

import com.backstage.system.domain.seckill.OshSeckillGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀商品池 Mapper 接口
 *
 * @author backstage
 * @date 2026-04-28
 */
public interface OshSeckillGoodsMapper {

    /**
     * 根据ID查询秒杀商品
     */
    OshSeckillGoods selectSeckillGoodsById(Long id);

    /**
     * 查询秒杀商品列表
     */
    List<OshSeckillGoods> selectSeckillGoodsList(OshSeckillGoods goods);

    /**
     * 新增秒杀商品
     */
    int insertSeckillGoods(OshSeckillGoods goods);

    /**
     * 修改秒杀商品
     */
    int updateSeckillGoods(OshSeckillGoods goods);

    /**
     * 批量更新秒杀商品状态
     *
     * @param ids    主键ID列表
     * @param status 目标状态：1-上架 2-下架
     */
    int updateSeckillGoodsStatusByIds(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 逻辑删除秒杀商品（单条）
     */
    int deleteSeckillGoodsById(Long id);

    /**
     * 批量逻辑删除秒杀商品
     */
    int deleteSeckillGoodsByIds(Long[] ids);

    /**
     * 根据 goods_id 和 goods_type 查询是否已入池（防重复入池校验）
     */
    OshSeckillGoods selectByGoodsIdAndType(@Param("goodsId") Long goodsId, @Param("goodsType") Integer goodsType);
}
