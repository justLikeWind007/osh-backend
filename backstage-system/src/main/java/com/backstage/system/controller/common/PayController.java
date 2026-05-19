package com.backstage.system.controller.common;


import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.pay.OrderCheckoutReqVO;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import com.backstage.system.domain.vo.pay.PayCreateRespVO;
import com.backstage.system.domain.order.OrderPaymentInfo;
import com.backstage.system.domain.order.OrderStatusResult;
import com.backstage.system.service.order.OrderCheckoutService;
import com.backstage.system.service.order.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/pc/pay")
public class PayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderCheckoutService orderCheckoutService;


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('pay:create')")
    public PayCreateRespVO create(@Valid @RequestBody OrderCheckoutReqVO reqVO) {
        OrderCheckoutRespVO checkoutResult = orderCheckoutService.checkout(reqVO);

        PayCreateRespVO response = new PayCreateRespVO();
        BeanUtils.copyProperties(checkoutResult, response);
        response.setCode(1);
        response.setMsg("ok");
        response.setOutTradeNo(checkoutResult.getPaymentNo());
        OrderPaymentInfo payment = checkoutResult.getPayment();
        if (payment != null) {
            response.setQrcode(payment.getQrcode());
            response.setPayUrl(payment.getPayUrl());
        }
        return response;
    }

    /**
     * 查询订单支付状态（前端轮询）。
     */
    @GetMapping("/status")
    @PreAuthorize("hasAuthority('pay:status')")
    public OrderStatusResult status(@RequestParam String outTradeNo) {
        return orderService.getPaymentStatus(outTradeNo);
    }

    /**
     * 取消支付（用户主动取消）。
     */

    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('pay:cancel')")
    public R cancel(@RequestParam String outTradeNo) {
        orderService.cancelPayment(outTradeNo);
        return R.ok();
    }
}
