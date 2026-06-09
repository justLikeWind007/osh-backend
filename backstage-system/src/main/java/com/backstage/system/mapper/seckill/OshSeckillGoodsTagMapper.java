package com.backstage.system.mapper.seckill;

import com.backstage.system.domain.seckill.OshSeckillGoodsTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀商品标签字典 Mapper 接口
 *
 * @author backstage
 */
public interface OshSeckillGoodsTagMapper {

    /**
     * 根据标签名查询标签（用于新增时判断是否已存在，复用已有标签）
     */
    OshSeckillGoodsTag selectByTagName(@Param("tagName") String tagName);

    /**
     * 插入新标签
     */
    int insertTag(OshSeckillGoodsTag tag);

    /**
     * 根据商品ID查询该商品的标签名称列表（通过 rel 表关联）
     */
    List<String> selectTagNamesBySeckillGoodsId(@Param("seckillGoodsId") Long seckillGoodsId);

    /**
     * 批量查询多个商品的标签（含 seckillGoodsId，供批量回填使用，避免 N+1）
     */
    List<OshSeckillGoodsTagWithGoodsId> selectTagsBySeckillGoodsIds(@Param("ids") List<Long> ids);

    /**
     * 批量查询多个商品的标签名称列表（含 seckillGoodsId）
     * 内部 DTO，仅供 Mapper 使用
     */
    class OshSeckillGoodsTagWithGoodsId {
        private Long seckillGoodsId;
        private String tagName;

        public Long getSeckillGoodsId() { return seckillGoodsId; }
        public void setSeckillGoodsId(Long seckillGoodsId) { this.seckillGoodsId = seckillGoodsId; }

        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
    }
}
