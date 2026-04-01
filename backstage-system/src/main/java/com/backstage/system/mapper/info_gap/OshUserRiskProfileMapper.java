package com.backstage.system.mapper.info_gap;

import com.backstage.system.domain.user.risk.OshUserRiskProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshUserRiskProfileMapper{
    OshUserRiskProfile selectRiskByUserId(@Param("userId") Long userId);
}