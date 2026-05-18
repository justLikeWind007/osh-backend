package com.backstage.system.domain.info_gap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("osh_info_gap_tag_rel")
public class OshInfoGapTagRel {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 信息差ID
     */
    private Long infoGapId;
    /**
     * 信息差选用的标签ID
     */
    private Long gapTagId;
    /**
     * 同一信息差内排序
     */
    private Integer sortNo;
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

    public Long getInfoGapId() {
        return infoGapId;
    }

    public void setInfoGapId(Long infoGapId) {
        this.infoGapId = infoGapId;
    }

    public Long getGapTagId() {
        return gapTagId;
    }

    public void setGapTagId(Long gapTagId) {
        this.gapTagId = gapTagId;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
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
