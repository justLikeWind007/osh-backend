package com.backstage.system.domain.order.enums;

import java.util.Objects;

/**
 * 商品类型枚举
 */
public enum ProductTypeEnum {

    COURSE(1, "课程"),
    BOOK(2, "书籍"),
    COLUMN(3, "专栏"),
    DEMO(4, "演示"),
    TOOL(5, "工具");

    private final int code;
    private final String desc;

    ProductTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据商品类型编码获取枚举。
     *
     * @param code 商品类型编码
     * @return 商品类型枚举，未命中返回 null
     */
    public static ProductTypeEnum fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        for (ProductTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }
}
