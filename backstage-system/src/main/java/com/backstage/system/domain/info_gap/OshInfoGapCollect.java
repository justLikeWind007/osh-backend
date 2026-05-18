package com.backstage.system.domain.info_gap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("osh_info_gap_collect")
public class OshInfoGapCollect {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收藏者用户ID
     */
    private Long userId;

    /**
     * 被收藏信息差的作者ID
     */
    private Long targetUserId;

    /**
     * 被收藏的信息差ID
     */
    private Long infoGapId;

    /**
     * 信息差标题
     */
    private String infoGapTitle;

    /**
     * 信息差状态(0:未收藏,1:已收藏)
     */
    private Integer collectStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记(0:未删,1:已删)
     */
    @TableLogic
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

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Long getInfoGapId() {
        return infoGapId;
    }

    public void setInfoGapId(Long infoGapId) {
        this.infoGapId = infoGapId;
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

    public String getInfoGapTitle() {
        return infoGapTitle;
    }

    public void setInfoGapTitle(String infoGapTitle) {
        this.infoGapTitle = infoGapTitle;
    }

    public Integer getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(Integer collectStatus) {
        this.collectStatus = collectStatus;
    }
}
