package com.backstage.system.service.order;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.order.enums.ProductTypeEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单支付成功处理器注册表。
 */
@Component
public class OrderPaidHandlerRegistry {

    private final Map<String, OrderPaidHandler> handlerMap;

    /**
     * 构造处理器注册表。
     *
     * @param handlers 系统内所有支付成功处理器
     */
    public OrderPaidHandlerRegistry(List<OrderPaidHandler> handlers) {
        this.handlerMap = new HashMap<>();
        for (OrderPaidHandler handler : handlers) {
            String bizType = handler.bizType();
            try {
                ProductTypeEnum.fromName(bizType);
            } catch (IllegalArgumentException e) {
                throw new ServiceException("支付后置处理器业务类型非法, bizType=" + bizType);
            }
            handlerMap.put(bizType, handler);
        }
    }

    /**
     * 根据业务类型获取处理器。
     *
     * @param bizType 业务类型编码
     * @return 对应处理器
     */
    public OrderPaidHandler getHandler(String bizType) {
        OrderPaidHandler handler = handlerMap.get(bizType);
        if (handler == null) {
            throw new ServiceException("未找到支付后置处理器, bizType=" + bizType);
        }
        return handler;
    }

    /**
     * 执行支付成功后的后置处理。
     *
     * @param bizType 业务类型编码
     * @param orderNo 业务订单号
     */
    public void handle(String bizType, String orderNo) {
        getHandler(bizType).handle(orderNo);
    }
}
