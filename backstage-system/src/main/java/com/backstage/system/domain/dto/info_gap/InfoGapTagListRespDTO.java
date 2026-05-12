package com.backstage.system.domain.dto.info_gap;

import java.time.LocalDateTime;

public class InfoGapTagListRespDTO {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 标签名
     */
    private String tagName;
    /**
     * 状态(1启用,0禁用)
     */
    private Integer status;
    /**
     * 被引用次数
     */
    private Long tagUseCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTagUseCount() {
        return tagUseCount;
    }

    public void setTagUseCount(Long tagUseCount) {
        this.tagUseCount = tagUseCount;
    }
}
