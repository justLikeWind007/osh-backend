package com.backstage.system.service;

import com.backstage.system.domain.user.OshUser;
import com.backstage.system.service.audit.AuditIndexMessage;
import com.backstage.system.service.course.CourseIndexDeleteMessage;
import com.backstage.system.service.course.CourseIndexUpsertMessage;
import com.backstage.system.service.tool.ToolIndexDeleteMessage;
import com.backstage.system.service.tool.ToolIndexMessage;

public interface OutboxEventService {

    void saveCourseIndexEvent(Long courseId, CourseIndexUpsertMessage message, OshUser operator);

    void saveCourseIndexDeleteEvent(Long courseId, CourseIndexDeleteMessage message, OshUser operator);

    void saveToolIndexEvent(Long toolId, ToolIndexMessage message, OshUser operator);

    void saveToolIndexEvent(Long toolId, ToolIndexMessage message, String operator);

    void saveToolIndexDeleteEvent(Long toolId, ToolIndexDeleteMessage message, OshUser operator);

    /**
     * 保存审核状态同步事件。
     * 内部根据 resourceType 路由到对应模块的 topic（如 osh.course.index / osh.tool.index），
     * Flink 消费到 AUDIT_APPROVED / AUDIT_REJECTED eventType 时只做 ES partial update。
     */
    void saveAuditIndexEvent(com.backstage.common.enums.ResourceTypeEnum resourceType,
                             AuditIndexMessage message, String operator);
}
