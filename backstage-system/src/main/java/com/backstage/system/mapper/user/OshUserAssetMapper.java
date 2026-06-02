package com.backstage.system.mapper.user;

import com.backstage.system.domain.user.OshUserAsset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/15
 * Time: 21:09
 */
public interface OshUserAssetMapper extends BaseMapper<OshUserAsset> {
    int updatePointsAtomic(@Param("userId") Long userId,
                           @Param("delta") Long delta,
                           @Param("requireEnough") boolean requireEnough);
}
