package com.backstage.common.enums;

public enum ResourceStatusEnum {

    DRAFT(0, "草稿"),
    PENDING_AUDIT(2, "待审核"),
    PUBLISHED(4, "已发布"),
    OFFLINE(6, "已下架");

    private final Integer code;
    private final String desc;

    ResourceStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ResourceStatusEnum fromCode(Integer code) {
        for (ResourceStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的资源状态: " + code);
    }
}
