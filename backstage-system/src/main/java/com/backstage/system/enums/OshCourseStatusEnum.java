package com.backstage.system.enums;

public enum OshCourseStatusEnum {

    DRAFT(0, "草稿"),
    PENDING_AUDIT(2, "待审核"),
    PUBLISHED(4, "已发布"),
    OFF_SHELF(3, "已下架");

    private final Integer code;
    private final String desc;

    OshCourseStatusEnum(Integer code, String desc) {
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
