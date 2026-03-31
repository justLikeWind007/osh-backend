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
    private Integer type; // 1-好评, 2-中评, 3-差评
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getter and Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getInfoGapId() { return infoGapId; }
    public void setInfoGapId(Long infoGapId) { this.infoGapId = infoGapId; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}