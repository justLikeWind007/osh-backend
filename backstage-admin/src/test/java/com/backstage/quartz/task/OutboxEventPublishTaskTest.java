package com.backstage.quartz.task;

import com.backstage.system.domain.outbox.OshOutboxEvent;
import com.backstage.system.mapper.outbox.OshOutboxEventMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OutboxEventPublishTaskTest {

    @InjectMocks
    private OutboxEventPublishTask outboxEventPublishTask;

    @Mock
    private OshOutboxEventMapper outboxEventMapper;

    @Test
    public void shouldRecoverTimeoutEventsAndScanPendingEventsWhenRunningXxlJobHandler() {
        when(outboxEventMapper.recoverTimeoutSendingEvents(any())).thenReturn(2);
        when(outboxEventMapper.selectPendingEvents(eq(100))).thenReturn(Collections.emptyList());

        outboxEventPublishTask.publishPendingEvents();

        verify(outboxEventMapper).recoverTimeoutSendingEvents(any());
        verify(outboxEventMapper).selectPendingEvents(100);
    }

    @Test
    public void shouldSkipPublishWhenOutboxEventNotFoundById() {
        when(outboxEventMapper.selectEventById(88L)).thenReturn(null);

        outboxEventPublishTask.publishEventById(88L);

        verify(outboxEventMapper).selectEventById(88L);
        verify(outboxEventMapper, never()).markSending(any());
    }

    @Test
    public void shouldSkipPublishWhenMarkSendingFailed() {
        OshOutboxEvent event = new OshOutboxEvent();
        event.setId(99L);
        event.setEventId("evt-99");
        event.setTopic("topic-test");
        event.setMessageKey("key-99");
        when(outboxEventMapper.selectEventById(99L)).thenReturn(event);
        when(outboxEventMapper.markSending(99L)).thenReturn(0);

        outboxEventPublishTask.publishEventById(99L);

        verify(outboxEventMapper).markSending(99L);
        verify(outboxEventMapper, never()).markSent(any());
    }
}
