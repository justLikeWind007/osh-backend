package com.backstage.system.service.impl.outbox;

import com.backstage.common.utils.kafka.KafkaMessageUtil;
import com.backstage.system.domain.outbox.OshOutboxEvent;
import com.backstage.system.mapper.outbox.OshOutboxEventMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OutboxEventPublishTask {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPublishTask.class);
    private static final int BATCH_SIZE = 100;
    private static final int SENDING_TIMEOUT_MINUTES = 10;
    private static final int MAX_ERROR_LENGTH = 2000;

    @Autowired
    private OshOutboxEventMapper outboxEventMapper;

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void publishPendingEvents() {
        recoverTimeoutSendingEvents();
        List<OshOutboxEvent> events = outboxEventMapper.selectPendingEvents(BATCH_SIZE);
        log.info("本轮扫描待投递outbox事件数量: {}", events == null ? 0 : events.size());
        for (OshOutboxEvent event : events) {
            publishEvent(event);
        }
    }

    public void publishEventById(Long eventId) {
        OshOutboxEvent event = outboxEventMapper.selectEventById(eventId);
        if (event == null) {
            log.warn("未找到待立即投递outbox事件, id={}", eventId);
            return;
        }
        publishEvent(event);
    }

    private void recoverTimeoutSendingEvents() {
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(SENDING_TIMEOUT_MINUTES);
        int recovered = outboxEventMapper.recoverTimeoutSendingEvents(timeoutTime);
        if (recovered > 0) {
            log.warn("自动恢复超时SENDING outbox事件数量: {}", recovered);
        }
    }

    private void publishEvent(OshOutboxEvent event) {
        log.info("准备投递outbox事件, id={}, eventId={}, eventType={}, aggregateType={}, aggregateId={}, topic={}, messageKey={}, retryCount={}",
                event.getId(), event.getEventId(), event.getEventType(), event.getAggregateType(), event.getAggregateId(), event.getTopic(), event.getMessageKey(), event.getRetryCount());
        int locked = outboxEventMapper.markSending(event.getId());
        if (locked <= 0) {
            log.info("outbox事件抢占失败，跳过投递, id={}, eventId={}", event.getId(), event.getEventId());
            return;
        }
        try {
            log.info("开始发送outbox事件到Kafka, id={}, eventId={}, topic={}, messageKey={}", event.getId(), event.getEventId(), event.getTopic(), event.getMessageKey());
            KafkaMessageUtil.sendMessage(event.getTopic(), event.getMessageKey(), event.getPayload());
            outboxEventMapper.markSent(event.getId());
            log.info("outbox事件发送Kafka成功并标记SENT, id={}, eventId={}, topic={}, messageKey={}", event.getId(), event.getEventId(), event.getTopic(), event.getMessageKey());
        } catch (Exception ex) {
            log.warn("outbox事件发送Kafka异常, id={}, eventId={}, topic={}, messageKey={}, error={}", event.getId(), event.getEventId(), event.getTopic(), event.getMessageKey(), ex.getMessage(), ex);
            handlePublishFailure(event, ex);
        }
    }

    private void handlePublishFailure(OshOutboxEvent event, Exception ex) {
        int retryCount = event.getRetryCount() == null ? 0 : event.getRetryCount();
        int nextRetryCount = retryCount + 1;
        int maxRetryCount = event.getMaxRetryCount() == null ? 10 : event.getMaxRetryCount();
        String errorMessage = truncateError(ex.getMessage());
        if (nextRetryCount >= maxRetryCount) {
            outboxEventMapper.markDead(event.getId(), errorMessage);
            log.error("outbox事件发送失败并进入DEAD, eventId={}, topic={}, error={}", event.getEventId(), event.getTopic(), errorMessage, ex);
            return;
        }
        outboxEventMapper.markRetry(event.getId(), nextRetryCount, nextRetryTime(nextRetryCount), errorMessage);
        log.warn("outbox事件发送失败等待重试, eventId={}, retryCount={}, topic={}, error={}", event.getEventId(), nextRetryCount, event.getTopic(), errorMessage, ex);
    }

    private LocalDateTime nextRetryTime(int retryCount) {
        if (retryCount <= 1) {
            return LocalDateTime.now().plusMinutes(1);
        }
        if (retryCount == 2) {
            return LocalDateTime.now().plusMinutes(5);
        }
        if (retryCount == 3) {
            return LocalDateTime.now().plusMinutes(15);
        }
        return LocalDateTime.now().plusHours(1);
    }

    private String truncateError(String errorMessage) {
        String value = StringUtils.defaultIfBlank(errorMessage, "发送Kafka消息失败");
        if (value.length() <= MAX_ERROR_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_ERROR_LENGTH);
    }
}
