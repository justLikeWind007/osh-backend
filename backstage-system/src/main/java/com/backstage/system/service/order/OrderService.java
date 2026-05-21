package com.backstage.system.service.order;

import com.backstage.system.domain.order.OshOrder;
import com.backstage.system.domain.vo.pay.OrderCheckoutReqVO;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import com.backstage.system.domain.order.OrderStatusResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface OrderService extends IService<OshOrder> {

    /**
     * 统一下单
     * @param reqVO
     * @return
     */
    OrderCheckoutRespVO checkout(OrderCheckoutReqVO reqVO);

    /**
     * 查询订单状态
     * @param orderNo
     * @return
     */
    OrderStatusResult getOrderStatus(String orderNo);

    /**
     * 查询支付状态
     * @param paymentNo
     * @return
     */
    OrderStatusResult getPaymentStatus(String paymentNo);

    /**
     * 处理支付回调
     * @param params
     * @return
     */
    boolean handlePayNotify(Map<String, String> params);

    /**
     * 取消支付
     * @param paymentNo
     */
    void cancelPayment(String paymentNo);

    /**
     * 按订单号取消支付
     * @param orderNo
     */
    void cancelPaymentByOrderNo(String orderNo);
}
