package com.backstage.system.domain.order.enums;

/**
 * 支付回调处理状态枚举
 */
public enum NotifyProcessStatusEnum {

    RECEIVED(0, "已接收"),
    SUCCESS(1, "处理成功"),
    FAILED(2, "处理失败"),
    IGNORED(3, "已忽略");

    private final int code;
    private final String desc;

    NotifyProcessStatusEnum(int code, String desc) {
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
