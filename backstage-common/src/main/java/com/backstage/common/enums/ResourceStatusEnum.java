package com.backstage.common.enums;

public enum ResourceStatusEnum {

    DRAFT(0, "草稿"),
    PENDING_AUDIT(2, "待审核"),
    PUBLISHED(4, "已发布"),
    OFF_SHELF(6, "已下架");

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

    public static boolean isDraft(Integer code) {
        return DRAFT.code.equals(code);
    }

    public static boolean isPendingAudit(Integer code) {
        return PENDING_AUDIT.code.equals(code);
    }

    public static boolean isPublished(Integer code) {
        return PUBLISHED.code.equals(code);
    }

    public static boolean isOffShelf(Integer code) {
        return OFF_SHELF.code.equals(code);
    }
}
