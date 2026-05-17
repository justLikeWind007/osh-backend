package com.backstage.system.mapper.order;

import com.backstage.system.domain.order.OshOrder;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface OshOrderMapper {

    int insertOshOrder(OshOrder order);

    OshOrder selectByOrderNo(@Param("orderNo") String orderNo);

    int updatePendingToPaid(@Param("orderNo") String orderNo, @Param("paidTime") LocalDateTime paidTime);

    int updatePendingToClosed(@Param("orderNo") String orderNo, @Param("closeTime") LocalDateTime closeTime);
}
