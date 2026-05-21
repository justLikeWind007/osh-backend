package com.backstage.common.enums;

public enum AnnouncementChannelEnum {

    SYSTEM_NOTICE(1, "系统通知"),
    USER_NOTICE(2, "业务公告");

    private final Integer code;
    private final String desc;

    AnnouncementChannelEnum(Integer code, String desc) {
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
