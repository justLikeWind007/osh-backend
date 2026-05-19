package com.backstage.system.mapper.tool;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshToolQuotaMapper {

    int increaseUserToolQuota(@Param("toolId") Long toolId,
                              @Param("userId") Long userId,
                              @Param("addCount") Integer addCount,
                              @Param("operator") String operator);

    int insertUserToolQuota(@Param("userId") Long userId,
                            @Param("toolId") Long toolId,
                            @Param("initCount") Integer initCount,
                            @Param("operator") String operator);
}
