package com.backstage.system.service;

import com.backstage.system.domain.outbox.OshOutboxEvent;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.outbox.OshOutboxEventMapper;
import com.backstage.system.service.course.CourseIndexUpsertMessage;
import com.backstage.system.service.impl.outbox.OutboxEventPublishTask;
import com.backstage.system.service.impl.outbox.OutboxEventServiceImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OutboxEventServiceImplTest {

    @InjectMocks
    private OutboxEventServiceImpl outboxEventService;

    @Mock
    private OshOutboxEventMapper outboxEventMapper;

    @Mock
    private OutboxEventPublishTask outboxEventPublishTask;

    @After
    public void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    public void shouldPublishOutboxEventAfterTransactionCommitWhenSavingCourseIndexCreateEvent() {
        when(outboxEventMapper.insertOutboxEvent(any(OshOutboxEvent.class))).thenAnswer(invocation -> {
            OshOutboxEvent event = invocation.getArgument(0);
            event.setId(55L);
            return 1;
        });
        TransactionSynchronizationManager.initSynchronization();

        outboxEventService.saveCourseIndexCreateEvent(10001L, new CourseIndexUpsertMessage(), buildOperator());

        verify(outboxEventPublishTask, never()).publishEventById(55L);
        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }
        verify(outboxEventPublishTask).publishEventById(55L);
    }

    @Test
    public void shouldPublishOutboxEventImmediatelyWhenNoTransactionSynchronizationExists() {
        when(outboxEventMapper.insertOutboxEvent(any(OshOutboxEvent.class))).thenAnswer(invocation -> {
            OshOutboxEvent event = invocation.getArgument(0);
            event.setId(56L);
            return 1;
        });

        outboxEventService.saveCourseIndexCreateEvent(10002L, new CourseIndexUpsertMessage(), buildOperator());

        verify(outboxEventPublishTask).publishEventById(56L);
    }

    private OshUser buildOperator() {
        OshUser operator = new OshUser();
        operator.setId(1L);
        operator.setUsername("admin");
        return operator;
    }
}
