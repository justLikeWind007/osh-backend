package com.backstage.system.domain.order.enums;

/**
 * 支付流水状态枚举
 */
public enum PaymentStatusEnum {

    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    CLOSED(3, "已关闭");

    private final int code;
    private final String desc;

    PaymentStatusEnum(int code, String desc) {
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
