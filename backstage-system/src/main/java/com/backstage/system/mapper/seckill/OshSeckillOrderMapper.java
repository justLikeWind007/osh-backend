package com.backstage.system.mapper.seckill;

import com.backstage.system.domain.seckill.OshSeckillOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀订单 Mapper 接口
 *
 * @author backstage
 * @date 2026-04-28
 */
public interface OshSeckillOrderMapper {

    /** 根据ID查询 */
    OshSeckillOrder selectOrderById(Long id);

    /** 根据秒杀订单号查询 */
    OshSeckillOrder selectOrderBySeckillNo(String seckillNo);

    /** 根据活动ID、明细ID和用户ID查询（判断是否已参与该活动内的某个商品） */
    OshSeckillOrder selectOrderByActivityAndUser(@Param("activityId") Long activityId,
                                                  @Param("itemId") Long itemId,
                                                  @Param("userId") Long userId);

    /** 管理端列表查询（支持多条件筛选） */
    List<OshSeckillOrder> selectOrderList(OshSeckillOrder order);

    /** 新增秒杀订单 */
    int insertOrder(OshSeckillOrder order);

    /** 修改秒杀订单 */
    int updateOrder(OshSeckillOrder order);
}
