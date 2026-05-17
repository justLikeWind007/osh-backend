package com.backstage.system.service.order;

import com.backstage.system.domain.order.OshPayment;
import com.backstage.system.mapper.order.OshPaymentMapper;
import com.backstage.system.service.order.impl.OrderCloseTask;
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
    private UnifiedOrderService unifiedOrderService;

    @Test
    public void shouldCloseExpiredPendingPaymentsEveryScan() {
        OshPayment p1 = new OshPayment();
        p1.setPaymentNo("P20260517010");
        OshPayment p2 = new OshPayment();
        p2.setPaymentNo("P20260517011");

        when(oshPaymentMapper.selectExpiredPendingPayments(any())).thenReturn(Arrays.asList(p1, p2));

        orderCloseTask.closeExpiredPendingPayments();

        verify(unifiedOrderService).cancelPayment("P20260517010");
        verify(unifiedOrderService).cancelPayment("P20260517011");
    }

    @Test
    public void shouldSkipWhenNoExpiredPendingPaymentsFound() {
        when(oshPaymentMapper.selectExpiredPendingPayments(any())).thenReturn(Collections.emptyList());

        orderCloseTask.closeExpiredPendingPayments();

        verify(unifiedOrderService, never()).cancelPayment(any());
    }
}
