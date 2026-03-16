package com.backstage.system.mapper.fava;

import java.util.List;

import com.backstage.system.domain.fava.OshFava;
import com.backstage.system.domain.vo.search.SearchResultVo;
import org.apache.ibatis.annotations.Param;

public interface OshFavaMapper {
    public List<SearchResultVo> selectFavaList(Long userId);

    public int insertFava(OshFava oshFava);

    public int deleteFava(OshFava oshFava);

    public int countFava(@Param("userId") Long userId, @Param("goodsId") Long goodsId, @Param("type") String type);
}