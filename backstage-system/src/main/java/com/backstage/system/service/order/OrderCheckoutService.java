package com.backstage.system.service.order;

import com.backstage.system.domain.vo.pay.OrderCheckoutReqVO;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;

/**
 * 订单结算服务 面向其他业务模块
 *
 * @author liudong
 */
public interface OrderCheckoutService {

    /**
     * 创建业务商品订单并发起支付。
     *
     * @param reqVO 订单结算参数
     * @return 订单结算结果
     * 供课程、电子书、会员等业务流程调用。
     */
    OrderCheckoutRespVO checkout(OrderCheckoutReqVO reqVO);
}
