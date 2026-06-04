package com.backstage.system.enums;

import com.backstage.common.enums.ResourceStatusEnum;

public enum OshCourseStatusEnum {

    DRAFT(ResourceStatusEnum.DRAFT.getCode(), ResourceStatusEnum.DRAFT.getDesc()),
    PENDING_AUDIT(ResourceStatusEnum.PENDING_AUDIT.getCode(), ResourceStatusEnum.PENDING_AUDIT.getDesc()),
    PUBLISHED(ResourceStatusEnum.PUBLISHED.getCode(), ResourceStatusEnum.PUBLISHED.getDesc()),
    OFF_SHELF(ResourceStatusEnum.OFF_SHELF.getCode(), ResourceStatusEnum.OFF_SHELF.getDesc()),
    // 创始人隐藏课程：与审核拒绝（OFF_SHELF=6）区分，独立状态 7
    HIDDEN(7, "已隐藏");

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
