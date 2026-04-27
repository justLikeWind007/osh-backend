package com.backstage.system.domain.user;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/23
 * Time: 20:10
 */
public class OshUserAssetRecord extends OSHBaseEntity {
    /**
     * 记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

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
     * 变动前余额
     */
    private Long beforeBalance;

    /**
     * 变动后余额
     */
    private Long afterBalance;

    /**
     * 备注说明
     */
    private String remark;

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

    public Long getBeforeBalance() {
        return beforeBalance;
    }

    public void setBeforeBalance(Long beforeBalance) {
        this.beforeBalance = beforeBalance;
    }

    public Long getAfterBalance() {
        return afterBalance;
    }

    public void setAfterBalance(Long afterBalance) {
        this.afterBalance = afterBalance;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "OshUserAssetRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", changeType=" + changeType +
                ", changeSource=" + changeSource +
                ", assetType=" + assetType +
                ", changeAmount=" + changeAmount +
                ", beforeBalance=" + beforeBalance +
                ", afterBalance=" + afterBalance +
                ", remark='" + remark + '\'' +
                '}';
    }
}
