package com.backstage.system.service.fava;

import java.util.List;

import com.backstage.system.domain.fava.OshFava;
import com.backstage.system.domain.vo.search.SearchResultVo;

public interface IOshFavaService {
    /**
     * 查询用户收藏列表
     */
    public List<SearchResultVo> selectFavaList(Long userId);

    /**
     * 添加收藏
     */
    public int insertFava(OshFava oshFava);

    /**
     * 取消收藏
     */
    public int deleteFava(OshFava oshFava);
    
    /**
     * 检查是否已收藏
     */
    public boolean isFava(Long userId, Long goodsId, String type);
}