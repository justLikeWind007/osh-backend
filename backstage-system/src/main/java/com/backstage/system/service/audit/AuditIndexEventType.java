package com.backstage.system.service.audit;

/**
 * 审核状态同步事件类型常量。
 * Flink 消费 osh.audit.status.sync topic 时，根据此 eventType 做 ES partial update。
 */
public final class AuditIndexEventType {

    /** 审核通过，资源状态变为 4（已发布） */
    public static final String AUDIT_APPROVED = "AUDIT_APPROVED";

    /** 审核拒绝，资源状态变为 6（已拒绝） */
    public static final String AUDIT_REJECTED = "AUDIT_REJECTED";

    private AuditIndexEventType() {
    }
}