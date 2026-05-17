package com.backstage.system.domain.info_gap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("osh_info_gap_tag")
public class OshInfoGapTag {

    @TableId(type = IdType.AUTO)
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    /**
     * 逻辑删除标记(0未删,1已删)
     */
    @TableLogic
    private Integer deleteFlag;

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}
