package com.backstage.system.domain.user.risk;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 用户风控记录
 */
@TableName("osh_user_risk_profile")
public class OshUserRiskProfile {
    @TableId(type = IdType.INPUT)
    private Long userId;
    private Integer warningCount;
    private Integer isGrey;
    private Integer isBanned;
    private String lastWarningReason;
    private LocalDateTime updateTime;

    // Getter and Setter (篇幅原因简写，实际需补全)
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getWarningCount() { return warningCount; }
    public void setWarningCount(Integer warningCount) { this.warningCount = warningCount; }
    public Integer getIsBanned() { return isBanned; }
    public void setIsBanned(Integer isBanned) { this.isBanned = isBanned; }
}