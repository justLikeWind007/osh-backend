package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/26
 * Time: 12:09
 */
@ApiModel(description = "用户资产变化实体类")
public class UserAssetDTO {
    /**
     * 变动类型：0-收入，1-支出
     */
    private Integer changeType;
    /**
     * 变动来源：0-签到，1-观看视频，2-分享，3-购买商品，4-提现，5-管理员调整等
     */
    private Integer changeSource;
    /**
     * 资产类型：0-金币，1-积分
     */
    private Integer assetType;
    /**
     * 变动数量（正数）
     */
    private Long changeAmount;
    /**
     * 备注说明
     */
    private String remark;

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public Integer getChangeSource() {
        return changeSource;
    }

    public void setChangeSource(Integer changeSource) {
        this.changeSource = changeSource;
    }

    public Integer getAssetType() {
        return assetType;
    }

    public void setAssetType(Integer assetType) {
        this.assetType = assetType;
    }

    public Long getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Long changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "UserAssetDTO{" +
                "changeType=" + changeType +
                ", changeSource=" + changeSource +
                ", assetType=" + assetType +
                ", changeAmount=" + changeAmount +
                ", remark='" + remark + '\'' +
                '}';
    }
}
