package com.backstage.system.domain.tool;

import com.backstage.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 工具收藏对象 osh_tool_collection
 */
@TableName("osh_tool_collection")
public class OshToolCollection extends BaseEntity {

    private Long id;
    private Long userId;
    private Long toolId;
    private Integer deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
