package com.backstage.system.service.order;


import com.backstage.system.domain.vo.order.GoodsVo;

public interface OrderListInfoService {
    public GoodsVo getOrderInfo(String type, Integer id);
}
