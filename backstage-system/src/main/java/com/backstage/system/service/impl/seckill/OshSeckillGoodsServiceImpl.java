package com.backstage.system.service.impl.seckill;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.dto.seckill.SeckillGoodsAddDTO;
import com.backstage.system.domain.dto.seckill.SeckillGoodsUpdateDTO;
import com.backstage.system.domain.seckill.OshSeckillGoods;
import com.backstage.system.domain.vo.seckill.SeckillGoodsVO;
import com.backstage.system.mapper.seckill.OshSeckillGoodsMapper;
import com.backstage.system.service.seckill.IOshSeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀商品池 Service 实现
 *
 * @author backstage
 * @date 2026-04-28
 */
@Service
public class OshSeckillGoodsServiceImpl implements IOshSeckillGoodsService {

    @Autowired
    private OshSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 实体转 VO（过滤 deleteFlag、createBy 等内部字段）
     */
    private SeckillGoodsVO toVO(OshSeckillGoods goods) {
        if (goods == null) return null;
        SeckillGoodsVO vo = new SeckillGoodsVO();
        vo.setId(goods.getId());
        vo.setGoodsId(goods.getGoodsId());
        vo.setGoodsType(goods.getGoodsType());
        vo.setGoodsName(goods.getGoodsName());
        vo.setGoodsCover(goods.getGoodsCover());
        vo.setOriginPrice(goods.getOriginPrice());
        vo.setMinSeckillPrice(goods.getMinSeckillPrice());
        vo.setStatus(goods.getStatus());
        vo.setSort(goods.getSort());
        vo.setCreateTime(goods.getCreateTime());
        vo.setUpdateTime(goods.getUpdateTime());
        return vo;
    }

    @Override
    public SeckillGoodsVO selectSeckillGoodsById(Long id) {
        return toVO(seckillGoodsMapper.selectSeckillGoodsById(id));
    }

    @Override
    public List<SeckillGoodsVO> selectSeckillGoodsList(OshSeckillGoods goods) {
        return seckillGoodsMapper.selectSeckillGoodsList(goods)
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 新增秒杀商品（入池）
     * DTO → Entity，校验重复入池
     */
    @Override
    public int insertSeckillGoods(SeckillGoodsAddDTO dto) {
        // 校验同一商品是否已入池
        OshSeckillGoods exist = seckillGoodsMapper.selectByGoodsIdAndType(
                dto.getGoodsId(), dto.getGoodsType());
        if (exist != null) {
            throw new ServiceException("该商品已在秒杀商品池中，请勿重复添加");
        }
        // DTO 转 Entity
        OshSeckillGoods goods = new OshSeckillGoods();
        goods.setGoodsId(dto.getGoodsId());
        goods.setGoodsType(dto.getGoodsType());
        goods.setGoodsName(dto.getGoodsName());
        goods.setGoodsCover(dto.getGoodsCover());
        goods.setOriginPrice(dto.getOriginPrice());
        goods.setMinSeckillPrice(dto.getMinSeckillPrice());
        goods.setSort(dto.getSort() != null ? dto.getSort() : 0);
        goods.setStatus(0); // 默认待审核，不允许前端传入
        return seckillGoodsMapper.insertSeckillGoods(goods);
    }

    /**
     * 修改秒杀商品信息
     * DTO → Entity，只更新允许修改的字段
     */
    @Override
    public int updateSeckillGoods(SeckillGoodsUpdateDTO dto) {
        OshSeckillGoods exist = seckillGoodsMapper.selectSeckillGoodsById(dto.getId());
        if (exist == null) {
            throw new ServiceException("秒杀商品不存在");
        }
        // DTO 转 Entity，只设置允许修改的字段
        OshSeckillGoods goods = new OshSeckillGoods();
        goods.setId(dto.getId());
        goods.setGoodsName(dto.getGoodsName());
        goods.setGoodsCover(dto.getGoodsCover());
        goods.setOriginPrice(dto.getOriginPrice());
        goods.setMinSeckillPrice(dto.getMinSeckillPrice());
        goods.setSort(dto.getSort());
        return seckillGoodsMapper.updateSeckillGoods(goods);
    }

    /**
     * 批量修改秒杀商品状态（上架/下架）
     */
    @Override
    public int updateSeckillGoodsStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("商品ID列表不能为空");
        }
        return seckillGoodsMapper.updateSeckillGoodsStatusByIds(ids, status);
    }

    /**
     * 批量逻辑删除秒杀商品
     */
    @Override
    public int deleteSeckillGoodsByIds(Long[] ids) {
        return seckillGoodsMapper.deleteSeckillGoodsByIds(ids);
    }
}
