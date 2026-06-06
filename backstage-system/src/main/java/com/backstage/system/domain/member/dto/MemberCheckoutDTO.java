package com.backstage.system.domain.member.dto;

import javax.validation.constraints.NotNull;

public class MemberCheckoutDTO {
    @NotNull(message = "套餐ID不能为空")
    private Long planId;
    private String channel;
    private Boolean usePoints;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public Boolean getUsePoints() { return usePoints; }
    public void setUsePoints(Boolean usePoints) { this.usePoints = usePoints; }
}
