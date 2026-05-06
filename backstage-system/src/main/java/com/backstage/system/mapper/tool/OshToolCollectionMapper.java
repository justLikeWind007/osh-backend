package com.backstage.system.mapper.tool;

import com.backstage.system.domain.tool.OshToolCollection;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface OshToolCollectionMapper extends BaseMapper<OshToolCollection> {

    OshToolCollection selectByUserIdAndToolId(@Param("userId") Long userId, @Param("toolId") Long toolId);

    int insertToolCollection(OshToolCollection collection);

    int updateCollectionDeleteFlag(@Param("id") Long id,
                                   @Param("deleteFlag") Integer deleteFlag,
                                   @Param("operator") String operator);

    default List<Long> selectActiveToolIdsByUserIdAndToolIds(Long userId, List<Long> toolIds) {
        if (userId == null || toolIds == null || toolIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<OshToolCollection> queryWrapper = new LambdaQueryWrapper<OshToolCollection>()
                .select(OshToolCollection::getToolId)
                .eq(OshToolCollection::getUserId, userId)
                .eq(OshToolCollection::getDeleteFlag, 0)
                .in(OshToolCollection::getToolId, toolIds);

        return selectList(queryWrapper).stream()
                .map(OshToolCollection::getToolId)
                .collect(Collectors.toList());
    }
}
