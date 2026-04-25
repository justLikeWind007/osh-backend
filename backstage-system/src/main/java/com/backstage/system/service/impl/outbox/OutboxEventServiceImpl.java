package com.backstage.system.service.impl.outbox;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.constant.KafkaConstants;
import com.backstage.system.domain.outbox.OshOutboxEvent;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.enums.outbox.OutboxEventStatusEnum;
import com.backstage.system.mapper.outbox.OshOutboxEventMapper;
import com.backstage.system.service.OutboxEventService;
import com.backstage.system.service.course.CourseIndexUpsertMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OutboxEventServiceImpl implements OutboxEventService {

    private static final String AGGREGATE_TYPE_COURSE = "COURSE";
    private static final String EVENT_TYPE_COURSE_INDEX_CREATE = "COURSE_INDEX_CREATE";
    private static final int DEFAULT_RETRY_COUNT = 0;
    private static final int DEFAULT_MAX_RETRY_COUNT = 10;
    private static final int NORMAL_DELETE_FLAG = 0;

    @Autowired
    private OshOutboxEventMapper outboxEventMapper;

    @Override
    public void saveCourseIndexCreateEvent(Long courseId, CourseIndexUpsertMessage message, OshUser operator) {
        LocalDateTime now = LocalDateTime.now();
        String operatorName = operator == null ? null : StringUtils.trimToNull(operator.getUsername());

        OshOutboxEvent event = new OshOutboxEvent();
        event.setEventId(UUID.randomUUID().toString().replace("-", ""));
        event.setAggregateType(AGGREGATE_TYPE_COURSE);
        event.setAggregateId(courseId);
        event.setEventType(EVENT_TYPE_COURSE_INDEX_CREATE);
        event.setTopic(KafkaConstants.COURSE_INDEX_CREATE_TOPIC);
        event.setMessageKey("course:" + courseId);
        event.setPayload(JSON.toJSONString(message));
        event.setStatus(OutboxEventStatusEnum.PENDING.getCode());
        event.setRetryCount(DEFAULT_RETRY_COUNT);
        event.setMaxRetryCount(DEFAULT_MAX_RETRY_COUNT);
        event.setNextRetryTime(now);
        event.setCreateBy(operatorName);
        event.setCreateTime(now);
        event.setUpdateBy(operatorName);
        event.setUpdateTime(now);
        event.setDeleteFlag(NORMAL_DELETE_FLAG);
        outboxEventMapper.insertOutboxEvent(event);
    }
}
