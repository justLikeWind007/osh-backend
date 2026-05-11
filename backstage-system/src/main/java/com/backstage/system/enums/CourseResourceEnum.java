package com.backstage.system.enums;

/**
 * 课程资源访问类型
 *
 * @author Whiskey_Lau
 * @date 2026-01-XX
 */
public enum CourseResourceEnum {

    FREE("FREE", "免费"),
    CASH_ONLY("CASH_ONLY", "仅现金"),
    CASH_POINT("CASH_POINT", "现金&积分"),
    VIP("VIP", "VIP专属"),
    SMALL_CLASS("SMALL_CLASS", "小班专属"),
    INTERNAL("INTERNAL", "内部专属");

    private final String code;
    private final String desc;

    CourseResourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static CourseResourceEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        String normalizedCode = code.trim();
        for (CourseResourceEnum value : values()) {
            if (value.code.equalsIgnoreCase(normalizedCode)) {
                return value;
            }
        }
        return null;
    }
}
