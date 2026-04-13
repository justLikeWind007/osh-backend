package com.backstage.system.enums;

/**
 * 课程资源访问类型
 *
 * @author ruoyi
 * @date 2026-01-XX
 */
public enum CourseResourceEnum {

    FREE(1, "免费"),
    CASH_PAY(2, "现金付费"),
    POINTS_OR_CASH_PAY(3, "积分/现金付费"),
    VIP_ONLY(4, "vip专属"),
    SMALL_CLASS_ONLY(5, "小班专属"),
    INTERNAL_MEMBER_ONLY(6, "内部成员专属");

    private final Integer code;
    private final String desc;

    CourseResourceEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static CourseResourceEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CourseResourceEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
