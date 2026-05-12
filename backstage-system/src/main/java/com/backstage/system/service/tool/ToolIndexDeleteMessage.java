package com.backstage.system.service.tool;

public class ToolIndexDeleteMessage {

    private String eventType = ToolIndexEventType.TOOL_INDEX_DELETE;
    private Long id;

    public ToolIndexDeleteMessage() {
    }

    public ToolIndexDeleteMessage(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
