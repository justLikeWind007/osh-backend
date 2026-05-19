package com.backstage.system.service.order.handler;

import com.backstage.system.domain.order.enums.ProductTypeEnum;
import com.backstage.system.service.book.IBookService;
import com.backstage.system.service.order.OrderPaidHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 电子书支付成功处理器。
 */
@Component
public class BookPaidHandler implements OrderPaidHandler {

    @Lazy
    @Resource
    private IBookService bookService;

    /**
     * 获取电子书业务类型编码。
     *
     * @return 商品类型标识
     */
    @Override
    public String bizType() {
        return ProductTypeEnum.BOOK.getName();
    }

    /**
     * 发放电子书访问权限。
     *
     * @param orderNo 业务订单号
     */
    @Override
    public void handle(String orderNo) {
        bookService.grantBookAccess(orderNo);
    }
}
