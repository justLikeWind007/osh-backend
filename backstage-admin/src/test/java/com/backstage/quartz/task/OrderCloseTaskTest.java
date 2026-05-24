package com.backstage.quartz.task;

import com.backstage.system.domain.order.OshPayment;
import com.backstage.system.mapper.order.OshPaymentMapper;
import com.backstage.system.service.order.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderCloseTaskTest {

    @InjectMocks
    private OrderCloseTask orderCloseTask;

    @Mock
    private OshPaymentMapper oshPaymentMapper;

    @Mock
    private OrderService orderService;

    @Test
    public void shouldCloseExpiredPendingPaymentsWhenRunningXxlJobHandler() {
        OshPayment p1 = new OshPayment();
        p1.setPaymentNo("P20260524010");
        OshPayment p2 = new OshPayment();
        p2.setPaymentNo("P20260524011");

        when(oshPaymentMapper.selectExpiredPendingPayments(any())).thenReturn(Arrays.asList(p1, p2));

        orderCloseTask.closeExpiredPendingPayments();

        verify(oshPaymentMapper).selectExpiredPendingPayments(any());
        verify(orderService).cancelPayment("P20260524010");
        verify(orderService).cancelPayment("P20260524011");
    }

    @Test
    public void shouldSkipCloseWhenNoExpiredPendingPaymentsFound() {
        when(oshPaymentMapper.selectExpiredPendingPayments(any())).thenReturn(Collections.emptyList());

        orderCloseTask.closeExpiredPendingPayments();

        verify(oshPaymentMapper).selectExpiredPendingPayments(any());
        verify(orderService, never()).cancelPayment(any());
    }
}
