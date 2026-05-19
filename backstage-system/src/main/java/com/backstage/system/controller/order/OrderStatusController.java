package com.backstage.system.controller.order;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.order.OrderStatusResult;
import com.backstage.system.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderStatusController {

    @Autowired
    private OrderService orderService;

    @Anonymous
    @GetMapping("/status")
    public R<OrderStatusResult> status(@RequestParam("order_no") String orderNo) {
        return R.ok(orderService.getOrderStatus(orderNo));
    }
}
