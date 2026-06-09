package com.backstage.system.service.audit;

import com.alibaba.fastjson2.annotation.JSONField;

import java.time.LocalDateTime;

/**
 * 审核状态同步消息体。
 * 只携带 ES partial update 所需的最小字段：resourceType、id、status、updateTime、updateBy。
 * Flink 消费后通过 resourceType 从 ResourceTypeEnum 查到 esIndexName，统一做局部更新。
 */
public class AuditIndexMessage {

    /** 事件类型：AUDIT_APPROVED / AUDIT_REJECTED */
    private String eventType;

    /** 资源类型，对应 ResourceTypeEnum.type，如 course / tool */
    private String resourceType;

    /** 资源 ID */
    private Long id;

    /** 审核后的资源状态：4-通过，6-拒绝 */
    private Integer status;

    /** 审核操作人 */
    private String updateBy;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updateTime;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}