package com.backstage.system.mapper.user;

import com.backstage.system.domain.user.OshUserAsset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/15
 * Time: 21:09
 */
public interface OshUserAssetMapper extends BaseMapper<OshUserAsset> {

    /**
     * 按用户锁定资产行，用于订单积分抵扣防止并发超扣。
     *
     * @param userId 用户ID
     * @return 用户资产
     */
    @Select("SELECT user_id, points FROM osh_user_asset WHERE user_id = #{userId}")
    OshUserAsset selectByUserIdForUpdate(@Param("userId") Long userId);
}
