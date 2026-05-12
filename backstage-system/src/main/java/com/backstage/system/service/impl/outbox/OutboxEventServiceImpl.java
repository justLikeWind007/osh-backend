package com.backstage.system.service.impl.outbox;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.constant.KafkaConstants;
import com.backstage.system.domain.outbox.OshOutboxEvent;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.enums.outbox.OutboxEventStatusEnum;
import com.backstage.system.mapper.outbox.OshOutboxEventMapper;
import com.backstage.system.service.OutboxEventService;
import com.backstage.system.service.course.CourseIndexDeleteMessage;
import com.backstage.system.service.course.CourseIndexUpsertMessage;
import com.backstage.system.service.tool.ToolIndexDeleteMessage;
import com.backstage.system.service.tool.ToolIndexMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OutboxEventServiceImpl implements OutboxEventService {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventServiceImpl.class);
    private static final String AGGREGATE_TYPE_COURSE = "COURSE";
    private static final String AGGREGATE_TYPE_TOOL = "TOOL";
    private static final String EVENT_TYPE_COURSE_INDEX_CREATE = "COURSE_INDEX_CREATE";
    private static final String EVENT_TYPE_COURSE_INDEX_UPDATE = "COURSE_INDEX_UPDATE";
    private static final String EVENT_TYPE_COURSE_INDEX_DELETE = "COURSE_INDEX_DELETE";
    private static final int DEFAULT_RETRY_COUNT = 0;
    private static final int DEFAULT_MAX_RETRY_COUNT = 10;
    private static final int NORMAL_DELETE_FLAG = 0;

    @Autowired
    private OshOutboxEventMapper outboxEventMapper;

    @Autowired
    private OutboxEventPublishTask outboxEventPublishTask;

    @Override
    public void saveCourseIndexCreateEvent(Long courseId, CourseIndexUpsertMessage message, OshUser operator) {
        saveCourseEvent(courseId, EVENT_TYPE_COURSE_INDEX_CREATE, KafkaConstants.COURSE_INDEX_CREATE_TOPIC,
                JSON.toJSONString(message), operator);
    }

    @Override
    public void saveCourseIndexUpdateEvent(Long courseId, CourseIndexUpsertMessage message, OshUser operator) {
        saveCourseEvent(courseId, EVENT_TYPE_COURSE_INDEX_UPDATE, KafkaConstants.COURSE_INDEX_UPDATE_TOPIC,
                JSON.toJSONString(message), operator);
    }

    @Override
    public void saveCourseIndexDeleteEvent(Long courseId, CourseIndexDeleteMessage message, OshUser operator) {
        saveCourseEvent(courseId, EVENT_TYPE_COURSE_INDEX_DELETE, KafkaConstants.COURSE_INDEX_DELETE_TOPIC,
                JSON.toJSONString(message), operator);
    }

    @Override
    public void saveToolIndexEvent(Long toolId, ToolIndexMessage message, OshUser operator) {
        String operatorName = operator == null ? null : operator.getUsername();
        saveToolIndexEvent(toolId, message, operatorName);
    }

    @Override
    public void saveToolIndexEvent(Long toolId, ToolIndexMessage message, String operator) {
        if (message == null || StringUtils.isBlank(message.getEventType())) {
            log.warn("工具索引消息为空或事件类型为空，跳过outbox写入, toolId={}", toolId);
            return;
        }
        saveEvent(AGGREGATE_TYPE_TOOL, toolId, message.getEventType(), KafkaConstants.TOOL_INDEX_TOPIC,
                JSON.toJSONString(message), "tool:" + toolId, operator);
    }

    @Override
    public void saveToolIndexDeleteEvent(Long toolId, ToolIndexDeleteMessage message, OshUser operator) {
        String operatorName = operator == null ? null : operator.getUsername();
        if (message == null || StringUtils.isBlank(message.getEventType())) {
            log.warn("工具索引删除消息为空或事件类型为空，跳过outbox写入, toolId={}", toolId);
            return;
        }
        saveEvent(AGGREGATE_TYPE_TOOL, toolId, message.getEventType(), KafkaConstants.TOOL_INDEX_TOPIC,
                JSON.toJSONString(message), "tool:" + toolId, operatorName);
    }

    private void saveCourseEvent(Long courseId, String eventType, String topic, String payload, OshUser operator) {
        String operatorName = operator == null ? null : operator.getUsername();
        saveEvent(AGGREGATE_TYPE_COURSE, courseId, eventType, topic, payload, "course:" + courseId, operatorName);
    }

    private void saveEvent(String aggregateType, Long aggregateId, String eventType, String topic,
                           String payload, String messageKey, String operator) {
        LocalDateTime now = LocalDateTime.now();
        String operatorName = StringUtils.trimToNull(operator);

        OshOutboxEvent event = new OshOutboxEvent();
        event.setEventId(UUID.randomUUID().toString().replace("-", ""));
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setEventType(eventType);
        event.setTopic(topic);
        event.setMessageKey(messageKey);
        event.setPayload(payload);
        event.setStatus(OutboxEventStatusEnum.PENDING.getCode());
        event.setRetryCount(DEFAULT_RETRY_COUNT);
        event.setMaxRetryCount(DEFAULT_MAX_RETRY_COUNT);
        event.setNextRetryTime(now);
        event.setCreateBy(operatorName);
        event.setCreateTime(now);
        event.setUpdateBy(operatorName);
        event.setUpdateTime(now);
        event.setDeleteFlag(NORMAL_DELETE_FLAG);
        int rows = outboxEventMapper.insertOutboxEvent(event);
        if (rows > 0) {
            publishAfterCommit(event.getId());
        }
    }

    private void publishAfterCommit(Long eventId) {
        if (eventId == null) {
            log.warn("outbox事件id为空，跳过提交后立即投递");
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishSafely(eventId);
                }
            });
            return;
        }
        publishSafely(eventId);
    }

    private void publishSafely(Long eventId) {
        try {
            outboxEventPublishTask.publishEventById(eventId);
            log.info("提交后立即投递outbox事件成功, id={}", eventId);
        } catch (Exception ex) {
            log.warn("提交后立即投递outbox事件异常，等待定时任务兜底, id={}, error={}", eventId, ex.getMessage(), ex);
        }
    }
}
