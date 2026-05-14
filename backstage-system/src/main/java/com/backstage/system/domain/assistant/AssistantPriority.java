package com.backstage.system.domain.assistant;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AI 助手反馈优先级枚举
 *
 * @author backstage
 */
public enum AssistantPriority {

    /**
     * 低优先级
     */
    LOW("low", "低优先级", 1),

    /**
     * 中优先级
     */
    MEDIUM("medium", "中优先级", 2),

    /**
     * 高优先级
     */
    HIGH("high", "高优先级", 3),

    /**
     * 紧急
     */
    URGENT("urgent", "紧急", 4);

    private final String code;
    private final String description;
    private final int level;

    private static final Map<String, AssistantPriority> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(AssistantPriority::getCode, Function.identity()));

    AssistantPriority(String code, String description, int level) {
        this.code = code;
        this.description = description;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    /**
     * 根据优先级码获取枚举
     *
     * @param code 优先级码
     * @return 枚举实例，不存在则返回 null
     */
    public static AssistantPriority fromCode(String code) {
        return CODE_MAP.get(code);
    }

    /**
     * 验证优先级码是否有效
     *
     * @param code 优先级码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return code != null && CODE_MAP.containsKey(code);
    }

    /**
     * 标准化优先级（兼容旧数据）
     *
     * @param priority 优先级字符串
     * @return 标准化后的优先级码
     */
    public static String normalize(String priority) {
        if (priority == null || priority.trim().isEmpty()) {
            return MEDIUM.getCode();
        }
        String normalized = priority.trim().toLowerCase();
        return CODE_MAP.getOrDefault(normalized, MEDIUM).getCode();
    }
}
