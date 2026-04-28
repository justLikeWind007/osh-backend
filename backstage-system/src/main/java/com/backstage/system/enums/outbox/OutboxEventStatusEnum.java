package com.backstage.system.enums.outbox;

public enum OutboxEventStatusEnum {

    PENDING(0, "待发送"),
    SENDING(1, "发送中"),
    SENT(2, "已发送"),
    DEAD(3, "死信");

    private final Integer code;
    private final String desc;

    OutboxEventStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
