package com.backstage.system.mapper.info_gap;

import com.backstage.system.domain.info_gap.OshInfoGapFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshInfoGapFollowMapper {

    /**
     * 手动查询关注记录
     */
    OshInfoGapFollow selectFollowRecord(@Param("userId") Long userId, @Param("targetUserId") Long targetUserId);

    /**
     * 手动删除关注记录
     */
    int deleteFollowRecord(@Param("id") Long id);

    /**
     * 手动插入关注记录
     */
    int insertFollowRecord(OshInfoGapFollow follow);
}