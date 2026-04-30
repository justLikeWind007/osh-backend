package com.backstage.system.domain.user.risk;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("osh_risk_log")
public class OshRiskLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long infoGapId;
    private Integer violationType; // 1-无效, 2-非法, 3-恶意
    private String remark;
    private Long operatorId;
    private LocalDateTime createTime;

    // Getter and Setter (按需补全)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getInfoGapId() { return infoGapId; }
    public void setInfoGapId(Long infoGapId) { this.infoGapId = infoGapId; }
    public Integer getViolationType() { return violationType; }
    public void setViolationType(Integer violationType) { this.violationType = violationType; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}