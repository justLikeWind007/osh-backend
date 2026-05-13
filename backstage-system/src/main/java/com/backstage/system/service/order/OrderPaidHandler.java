package com.backstage.system.service.order;

/**
 * 订单支付成功后的业务处理器。
 */
public interface OrderPaidHandler {

    /**
     * 获取业务类型标识。
     *
     * @return 业务类型标识
     */
    String bizType();

    /**
     * 处理支付成功后的后置逻辑。
     *
     * @param orderNo 业务订单号
     */
    void handle(String orderNo);
}
