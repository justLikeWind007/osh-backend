package com.backstage.system.domain.order.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatusEnum {

    PENDING(0, "待支付"),

    PAID(1, "已支付"),

    CANCELED(2, "已取消"),

    CLOSED(3, "已关闭")
    ;

    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
