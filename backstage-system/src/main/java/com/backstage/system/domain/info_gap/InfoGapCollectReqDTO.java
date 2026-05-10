package com.backstage.system.domain.info_gap;

public class InfoGapCollectReqDTO {

    private Long userId;
    private String username;
    private Long infoGapId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getInfoGapId() {
        return infoGapId;
    }

    public void setInfoGapId(Long infoGapId) {
        this.infoGapId = infoGapId;
    }
}
