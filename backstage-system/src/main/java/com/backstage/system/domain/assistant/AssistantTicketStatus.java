package com.backstage.system.domain.assistant;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AI 助手工单状态枚举
 *
 * @author backstage
 */
public enum AssistantTicketStatus {

    /**
     * 待处理
     */
    PENDING("PENDING", "已提交"),

    /**
     * 已受理
     */
    TRIAGED("TRIAGED", "已受理"),

    /**
     * 处理中
     */
    PROCESSING("PROCESSING", "处理中"),

    /**
     * 待用户确认
     */
    PENDING_CONFIRM("PENDING_CONFIRM", "待你确认"),

    /**
     * 已解决
     */
    RESOLVED("RESOLVED", "已解决"),

    /**
     * 重新打开
     */
    REOPENED("REOPENED", "问题仍在"),

    /**
     * 已关闭
     */
    CLOSED("CLOSED", "已关闭"),

    /**
     * 已驳回
     */
    REJECTED("REJECTED", "已驳回");

    private final String code;
    private final String description;

    private static final Map<String, AssistantTicketStatus> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(AssistantTicketStatus::getCode, Function.identity()));
    private static final Map<String, String> LEGACY_CODE_MAP;
    private static final Map<String, Set<String>> TRANSITION_MAP;

    static {
        Map<String, String> legacyCodeMap = new HashMap<>();
        legacyCodeMap.put("submitted", PENDING.getCode());
        legacyCodeMap.put("triaged", TRIAGED.getCode());
        legacyCodeMap.put("in_progress", PROCESSING.getCode());
        legacyCodeMap.put("pending_confirm", PENDING_CONFIRM.getCode());
        legacyCodeMap.put("resolved", RESOLVED.getCode());
        legacyCodeMap.put("closed", CLOSED.getCode());
        legacyCodeMap.put("rejected", REJECTED.getCode());
        legacyCodeMap.put("reopened", REOPENED.getCode());
        legacyCodeMap.put("pending", PENDING.getCode());
        legacyCodeMap.put("triage", TRIAGED.getCode());
        legacyCodeMap.put("processing", PROCESSING.getCode());
        legacyCodeMap.put("done", RESOLVED.getCode());
        LEGACY_CODE_MAP = Collections.unmodifiableMap(legacyCodeMap);

        Map<String, Set<String>> transitionMap = new HashMap<>();
        transitionMap.put(PENDING.getCode(), new HashSet<>(Arrays.asList(PROCESSING.getCode(), CLOSED.getCode(), REJECTED.getCode())));
        transitionMap.put(PROCESSING.getCode(), new HashSet<>(Arrays.asList(PENDING_CONFIRM.getCode(), CLOSED.getCode(), REJECTED.getCode())));
        transitionMap.put(PENDING_CONFIRM.getCode(), new HashSet<>(Arrays.asList(RESOLVED.getCode(), REOPENED.getCode(), CLOSED.getCode())));
        transitionMap.put(REOPENED.getCode(), new HashSet<>(Arrays.asList(PROCESSING.getCode(), CLOSED.getCode(), REJECTED.getCode())));
        transitionMap.put(RESOLVED.getCode(), new HashSet<>(Arrays.asList(REOPENED.getCode())));
        transitionMap.put(CLOSED.getCode(), new HashSet<>(Arrays.asList(REOPENED.getCode())));
        transitionMap.put(REJECTED.getCode(), new HashSet<>(Arrays.asList(REOPENED.getCode())));
        TRANSITION_MAP = Collections.unmodifiableMap(transitionMap);
    }

    AssistantTicketStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 枚举实例，不存在则返回 null
     */
    public static AssistantTicketStatus fromCode(String code) {
        String normalizedCode = normalize(code);
        return normalizedCode == null ? null : CODE_MAP.get(normalizedCode);
    }

    /**
     * 验证状态码是否有效
     *
     * @param code 状态码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }

    /**
     * 归一化状态编码，兼容历史小写状态。
     *
     * @param code 状态码
     * @return 归一化后的状态码
     */
    public static String normalize(String code) {
        if (code == null) {
            return null;
        }
        String trimmedCode = code.trim();
        if (trimmedCode.isEmpty()) {
            return null;
        }
        String upperCode = trimmedCode.toUpperCase();
        if (CODE_MAP.containsKey(upperCode)) {
            return upperCode;
        }
        return LEGACY_CODE_MAP.get(trimmedCode.toLowerCase());
    }

    /**
     * 获取状态展示文案。
     *
     * @param code 状态码
     * @return 状态文案
     */
    public static String getDescriptionByCode(String code) {
        AssistantTicketStatus status = fromCode(code);
        return status == null ? code : status.getDescription();
    }

    /**
     * 校验状态流转是否合法。
     *
     * @param fromCode 原状态
     * @param toCode   目标状态
     * @return 是否合法
     */
    public static boolean canTransfer(String fromCode, String toCode) {
        String normalizedFromCode = normalize(fromCode);
        String normalizedToCode = normalize(toCode);
        if (normalizedFromCode == null || normalizedToCode == null || normalizedFromCode.equals(normalizedToCode)) {
            return false;
        }
        Set<String> allowedStatusSet = TRANSITION_MAP.get(normalizedFromCode);
        return allowedStatusSet != null && allowedStatusSet.contains(normalizedToCode);
    }
}
