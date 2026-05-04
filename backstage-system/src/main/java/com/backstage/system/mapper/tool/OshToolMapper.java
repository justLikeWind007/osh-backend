package com.backstage.system.mapper.tool;

import com.backstage.system.domain.tool.OshTool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OshToolMapper {

    OshTool selectToolById(@Param("id") Long id);

    int insertTool(OshTool tool);

    int updateTool(OshTool tool);

    int deleteToolsByIds(@Param("ids") List<Long> ids, @Param("operator") String operator);

    int increaseCollectionCount(@Param("toolId") Long toolId);

    int decreaseCollectionCount(@Param("toolId") Long toolId);
}
