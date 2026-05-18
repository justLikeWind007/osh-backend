package com.backstage.common.enums;

/**
 * 网站类型枚举
 *
 * @author backstage
 */
public enum SiteTypeEnum {

    DEMO("demo", "演示站点");

    private final String code;
    private final String name;

    SiteTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据 code 获取枚举
     */
    public static SiteTypeEnum fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (SiteTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
