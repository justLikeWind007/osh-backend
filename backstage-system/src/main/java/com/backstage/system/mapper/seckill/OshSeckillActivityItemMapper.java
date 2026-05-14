package com.backstage.system.mapper.seckill;

import com.backstage.system.domain.seckill.OshSeckillActivityItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀活动商品明细 Mapper 接口
 *
 * @author backstage
 * @date 2026-05-04
 */
public interface OshSeckillActivityItemMapper {

    /** 根据ID查询明细 */
    OshSeckillActivityItem selectItemById(Long id);

    /** 根据活动ID查询所有明细（按 sort 升序） */
    List<OshSeckillActivityItem> selectItemsByActivityId(Long activityId);

    /**
     * 查询符合搜索条件的进行中活动ID列表
     * 用于用户端按商品名称、商品类型筛选活动
     */
    List<Long> selectActiveActivityIdsByCondition(@Param("title") String title,
                                                   @Param("goodsType") Integer goodsType);

    /** 批量插入明细 */
    int insertItems(@Param("items") List<OshSeckillActivityItem> items);

    /** 修改明细 */
    int updateItem(OshSeckillActivityItem item);

    /** 逻辑删除某活动下的所有明细 */
    int deleteItemsByActivityId(Long activityId);

    /** 批量逻辑删除指定明细 */
    int deleteItemsByIds(@Param("ids") List<Long> ids);

    /** 扣减库存 + 增加已售（原子更新，乐观锁兜底） */
    int decrStock(@Param("id") Long id, @Param("quantity") int quantity);
}
