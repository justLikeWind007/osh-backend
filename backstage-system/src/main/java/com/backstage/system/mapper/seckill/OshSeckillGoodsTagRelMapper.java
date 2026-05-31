package com.backstage.system.mapper.seckill;

import com.backstage.system.domain.seckill.OshSeckillGoodsTagRel;
import org.apache.ibatis.annotations.Param;

/**
 * 秒杀商品标签关联 Mapper 接口
 *
 * @author backstage
 */
public interface OshSeckillGoodsTagRelMapper {

    /**
     * 插入关联关系（忽略唯一键冲突，幂等）
     */
    int insertRel(OshSeckillGoodsTagRel rel);

    /**
     * 软删除某商品的所有标签关联（修改标签时先删再插）
     */
    int softDeleteBySeckillGoodsId(@Param("seckillGoodsId") Long seckillGoodsId,
                                   @Param("updateBy") String updateBy);
}
