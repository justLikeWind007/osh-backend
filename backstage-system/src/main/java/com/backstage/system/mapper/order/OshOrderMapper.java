package com.backstage.system.mapper.order;

import com.backstage.system.domain.order.OshOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OshOrderMapper extends BaseMapper<OshOrder> {

    int insertOshOrder(OshOrder order);

    OshOrder selectByOrderNo(@Param("orderNo") String orderNo);

    int updatePendingToPaid(@Param("orderNo") String orderNo, @Param("paidTime") LocalDateTime paidTime);

    int updatePendingToClosed(@Param("orderNo") String orderNo, @Param("closeTime") LocalDateTime closeTime);

    /**
     * 按用户ID查询订单列表（按创建时间倒序）
     */
    List<OshOrder> selectByUserId(@Param("userId") Long userId);

    /**
     * 按用户ID和状态查询订单列表（status 为 null 时查全部）
     */
    List<OshOrder> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);
}
