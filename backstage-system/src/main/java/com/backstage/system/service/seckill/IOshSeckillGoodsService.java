package com.backstage.system.service.seckill;

import com.backstage.system.domain.dto.seckill.SeckillGoodsAddDTO;
import com.backstage.system.domain.dto.seckill.SeckillGoodsUpdateDTO;
import com.backstage.system.domain.seckill.OshSeckillGoods;
import com.backstage.system.domain.vo.seckill.SeckillGoodsPreviewVO;
import com.backstage.system.domain.vo.seckill.SeckillGoodsVO;

import java.util.List;

/**
 * 秒杀商品池 Service 接口
 *
 * @author backstage
 * @date 2026-04-28
 */
public interface IOshSeckillGoodsService {

    /**
     * 根据ID查询秒杀商品（返回VO）
     */
    SeckillGoodsVO selectSeckillGoodsById(Long id);

    /**
     * 查询秒杀商品列表（返回VO集合）
     */
    List<SeckillGoodsVO> selectSeckillGoodsList(OshSeckillGoods goods);

    /**
     * 预览商品信息（根据 goodsType 路由到对应表查询，供前端表单自动回填）
     */
    SeckillGoodsPreviewVO previewGoods(Integer goodsType, Long goodsId);

    /**
     * 新增秒杀商品（入池），goodsName/goodsCover/originPrice 由后端自动查表填充
     */
    int insertSeckillGoods(SeckillGoodsAddDTO dto);

    /**
     * 修改秒杀商品信息
     */
    int updateSeckillGoods(SeckillGoodsUpdateDTO dto);

    /**
     * 批量修改秒杀商品状态（上架/下架）
     */
    int updateSeckillGoodsStatus(List<Long> ids, Integer status);

    /**
     * 批量逻辑删除秒杀商品
     */
    int deleteSeckillGoodsByIds(Long[] ids);
}
