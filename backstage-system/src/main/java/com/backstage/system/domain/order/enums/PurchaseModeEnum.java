package com.backstage.system.domain.order.enums;

/**
 * 购买方式枚举
 */
public enum PurchaseModeEnum {

    NORMAL(0, "普通购买"),
    GROUP(1, "拼团"),
    SECKILL(2, "秒杀"),
    PRESALE(3, "预售");

    private final int code;
    private final String desc;

    PurchaseModeEnum(int code, String desc) {
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
