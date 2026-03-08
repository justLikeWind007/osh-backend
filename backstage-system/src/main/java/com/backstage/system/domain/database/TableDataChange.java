package com.backstage.system.domain.database;

import java.util.List;
import java.util.Map;

public class TableDataChange {
    private List<Map<String, Object>> inserted; // 新增数据
    private List<Map<String, Object>> updated;  // 更新数据
    private List<Map<String, Object>> deleted;  // 删除数据

    // getters and setters
    public List<Map<String, Object>> getInserted() {
        return inserted;
    }

    public void setInserted(List<Map<String, Object>> inserted) {
        this.inserted = inserted;
    }

    public List<Map<String, Object>> getUpdated() {
        return updated;
    }

    public void setUpdated(List<Map<String, Object>> updated) {
        this.updated = updated;
    }

    public List<Map<String, Object>> getDeleted() {
        return deleted;
    }

    public void setDeleted(List<Map<String, Object>> deleted) {
        this.deleted = deleted;
    }
}