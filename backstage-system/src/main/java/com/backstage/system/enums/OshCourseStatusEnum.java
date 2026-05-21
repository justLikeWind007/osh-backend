package com.backstage.system.enums;

import com.backstage.common.enums.ResourceStatusEnum;

public enum OshCourseStatusEnum {

    DRAFT(ResourceStatusEnum.DRAFT.getCode(), ResourceStatusEnum.DRAFT.getDesc()),
    PENDING_AUDIT(ResourceStatusEnum.PENDING_AUDIT.getCode(), ResourceStatusEnum.PENDING_AUDIT.getDesc()),
    PUBLISHED(ResourceStatusEnum.PUBLISHED.getCode(), ResourceStatusEnum.PUBLISHED.getDesc()),
    OFF_SHELF(ResourceStatusEnum.OFF_SHELF.getCode(), ResourceStatusEnum.OFF_SHELF.getDesc());

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
