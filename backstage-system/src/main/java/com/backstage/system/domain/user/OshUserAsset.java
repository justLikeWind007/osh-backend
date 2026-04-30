package com.backstage.system.domain.user;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/15
 * Time: 21:02
 */
@TableName("osh_user_asset")
public class OshUserAsset extends OSHBaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;
    private Long goldCoin;
    private Long points;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoldCoin() {
        return goldCoin;
    }

    public void setGoldCoin(Long goldCoin) {
        this.goldCoin = goldCoin;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "OshUserAsset{" +
                "userId=" + userId +
                ", goldCoin=" + goldCoin +
                ", points=" + points +
                '}';
    }
}
