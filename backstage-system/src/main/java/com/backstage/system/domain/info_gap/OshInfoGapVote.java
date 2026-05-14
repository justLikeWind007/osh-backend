package com.backstage.system.domain.info_gap;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("osh_info_gap_vote")
public class OshInfoGapVote {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long infoGapId;
    private Integer voteType; // 1-好评, 2-中评, 3-差评
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer voteFlag;
    private Integer deleteFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getInfoGapId() { return infoGapId; }
    public void setInfoGapId(Long infoGapId) { this.infoGapId = infoGapId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public Integer getVoteType() {
        return voteType;
    }

    public void setVoteType(Integer voteType) {
        this.voteType = voteType;
    }

    public Integer getVoteFlag() {
        return voteFlag;
    }

    public void setVoteFlag(Integer voteFlag) {
        this.voteFlag = voteFlag;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}