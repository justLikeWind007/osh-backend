package com.backstage.system.mapper.outbox;

import com.backstage.system.domain.outbox.OshOutboxEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OshOutboxEventMapper {

    int insertOutboxEvent(OshOutboxEvent event);

    OshOutboxEvent selectEventById(@Param("id") Long id);

    List<OshOutboxEvent> selectPendingEvents(@Param("limit") int limit);

    int markSending(@Param("id") Long id);

    int markSent(@Param("id") Long id);

    int markRetry(@Param("id") Long id,
                  @Param("retryCount") Integer retryCount,
                  @Param("nextRetryTime") LocalDateTime nextRetryTime,
                  @Param("lastError") String lastError);

    int markDead(@Param("id") Long id, @Param("lastError") String lastError);

    int recoverTimeoutSendingEvents(@Param("timeoutTime") LocalDateTime timeoutTime);
}
