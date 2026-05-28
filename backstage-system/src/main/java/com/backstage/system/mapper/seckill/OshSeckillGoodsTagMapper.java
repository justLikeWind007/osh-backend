package com.backstage.system.mapper.seckill;

import com.backstage.system.domain.seckill.OshSeckillGoodsTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀商品标签 Mapper 接口
 *
 * @author backstage
 */
public interface OshSeckillGoodsTagMapper {

    /**
     * 查询单个商品的标签名称列表
     */
    List<String> selectTagNamesBySeckillGoodsId(@Param("seckillGoodsId") Long seckillGoodsId);

    /**
     * 批量查询多个商品的标签（返回实体，含 seckillGoodsId，供批量回填使用）
     */
    List<OshSeckillGoodsTag> selectTagsBySeckillGoodsIds(@Param("ids") List<Long> ids);

    /**
     * 插入单条标签
     */
    int insertTag(OshSeckillGoodsTag tag);

    /**
     * 批量插入标签
     */
    int insertTags(@Param("tags") List<OshSeckillGoodsTag> tags);

    /**
     * 软删除某商品的所有标签（修改标签时先删再插）
     */
    int softDeleteBySeckillGoodsId(@Param("seckillGoodsId") Long seckillGoodsId,
                                   @Param("updateBy") String updateBy);
}
