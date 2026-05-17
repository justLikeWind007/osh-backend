package com.backstage.system.mapper.order;

import com.backstage.system.domain.order.OshPaymentNotifyLogDO;
import org.apache.ibatis.annotations.Param;

public interface OshPaymentNotifyLogMapper {

    int insertOshPaymentNotifyLog(OshPaymentNotifyLogDO notifyLog);

    int updateProcessResult(@Param("id") Long id,
                            @Param("orderNo") String orderNo,
                            @Param("signValid") Integer signValid,
                            @Param("processStatus") Integer processStatus,
                            @Param("errorMsg") String errorMsg);
}
