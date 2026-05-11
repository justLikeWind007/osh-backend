package com.backstage.system.mapper.tool;

import com.backstage.system.domain.tool.OshToolVote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshToolVoteMapper {

    OshToolVote selectByUserIdAndToolId(@Param("userId") Long userId, @Param("toolId") Long toolId);

    Integer selectVoteType(@Param("userId") Long userId, @Param("toolId") Long toolId);

    int insertToolVote(OshToolVote vote);

    int updateToolVote(OshToolVote vote);

    int deleteToolVote(@Param("id") Long id, @Param("operator") String operator);
}
