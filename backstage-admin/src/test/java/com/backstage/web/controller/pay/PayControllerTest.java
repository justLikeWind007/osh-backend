package com.backstage.web.controller.pay;

import com.backstage.system.controller.pay.PayController;
import com.backstage.system.domain.order.OrderPaymentInfo;
import com.backstage.system.domain.order.OrderStatusResult;
import com.backstage.system.domain.vo.pay.OrderCheckoutReqVO;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import com.backstage.system.service.order.OrderCheckoutService;
import com.backstage.system.service.order.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PayControllerTest {

    private MockMvc mockMvc;
    private OrderService orderService;
    private OrderCheckoutService orderCheckoutService;

    @Before
    public void setUp() {
        PayController controller = new PayController();
        orderService = mock(OrderService.class);
        orderCheckoutService = mock(OrderCheckoutService.class);
        ReflectionTestUtils.setField(controller, "orderService", orderService);
        ReflectionTestUtils.setField(controller, "orderCheckoutService", orderCheckoutService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldWrapCreateResponseInStandardResult() throws Exception {
        OrderPaymentInfo paymentInfo = new OrderPaymentInfo();
        paymentInfo.setQrcode("weixin://pay/qr/123");
        paymentInfo.setPayUrl("https://pay.example.com/123");

        OrderCheckoutRespVO checkoutRespVO = new OrderCheckoutRespVO();
        checkoutRespVO.setNeedPay(true);
        checkoutRespVO.setOrderNo("O20260524001");
        checkoutRespVO.setPaymentNo("P20260524001");
        checkoutRespVO.setPayStatus("pending");
        checkoutRespVO.setPrice(new BigDecimal("19.90"));
        checkoutRespVO.setExpireTime("2026-05-24 16:30:00");
        checkoutRespVO.setCloseExpireMinutes(30);
        checkoutRespVO.setPayment(paymentInfo);

        when(orderCheckoutService.checkout(any(OrderCheckoutReqVO.class))).thenReturn(checkoutRespVO);

        mockMvc.perform(post("/pc/pay/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":9,\"productType\":5,\"productId\":1001,\"productName\":\"AI海报生成器\",\"originalAmount\":19.90,\"payableAmount\":19.90,\"channel\":\"wxpay\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderNo").value("O20260524001"))
                .andExpect(jsonPath("$.data.paymentNo").value("P20260524001"))
                .andExpect(jsonPath("$.data.outTradeNo").value("P20260524001"))
                .andExpect(jsonPath("$.data.expireTime").value("2026-05-24 16:30:00"))
                .andExpect(jsonPath("$.data.closeExpireMinutes").value(30))
                .andExpect(jsonPath("$.data.qrcode").value("weixin://pay/qr/123"))
                .andExpect(jsonPath("$.data.payUrl").value("https://pay.example.com/123"));
    }

    @Test
    public void shouldWrapOrderStatusResponseInStandardResult() throws Exception {
        OrderStatusResult statusResult = new OrderStatusResult();
        statusResult.setOrderNo("O20260524002");
        statusResult.setPaymentNo("P20260524002");
        statusResult.setOrderStatus(0);
        statusResult.setPaymentStatus(0);
        statusResult.setPayStatus(false);

        when(orderService.getOrderStatus("O20260524002")).thenReturn(statusResult);

        mockMvc.perform(get("/pc/pay/status").param("orderNo", "O20260524002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderNo").value("O20260524002"))
                .andExpect(jsonPath("$.data.paymentNo").value("P20260524002"))
                .andExpect(jsonPath("$.data.orderStatus").value(0))
                .andExpect(jsonPath("$.data.paymentStatus").value(0))
                .andExpect(jsonPath("$.data.payStatus").value(false));
    }

    @Test
    public void shouldWrapCancelResponseInStandardResult() throws Exception {
        mockMvc.perform(post("/pc/pay/cancel").param("orderNo", "O20260524003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("操作成功"));

        verify(orderService).cancelPaymentByOrderNo(eq("O20260524003"));
    }
}
