package com.backstage.system.mapper.info_gap;

import com.backstage.system.domain.info_gap.OshInfoGapVote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshInfoGapVoteMapper{
    int insertVoteRecord(OshInfoGapVote vote);

    void deleteVoteRecord(@Param("id")Long id);

    OshInfoGapVote selectVoteRecord(@Param("userId")Long userId,@Param("infoId")Long infoId);

    void updateVoteRecord(OshInfoGapVote existVote);

    void cancelVoteRecord(@Param("userId")Long userId, @Param("infoId")Long infoId);
}