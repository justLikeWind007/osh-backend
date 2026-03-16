package com.backstage.system.service.impl.fava;

import com.backstage.system.domain.fava.OshFava;
import com.backstage.system.domain.vo.search.SearchResultVo;
import com.backstage.system.mapper.fava.OshFavaMapper;
import com.backstage.system.service.fava.IOshFavaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OshFavaServiceImpl implements IOshFavaService {

    @Autowired
    private OshFavaMapper favaMapper;

    /**
     * 查询收藏列表 (复用搜索的VO)
     */
    @Override
    public List<SearchResultVo> selectFavaList(Long userId) {
        return favaMapper.selectFavaList(userId);
    }

    /**
     * 收藏操作：先检查是否收藏，防止重复提交
     */
    @Override
    public int insertFava(OshFava fava) {
        int count = favaMapper.countFava(fava.getUserId(), fava.getGoodsId(), fava.getType());
        if (count > 0) {
            return 1; // 已经收藏过了，直接返回成功或提示
        }
        return favaMapper.insertFava(fava);
    }

    /**
     * 取消收藏
     */
    @Override
    public int deleteFava(OshFava fava) {
        return favaMapper.deleteFava(fava);
    }

    /**
     * 判断是否收藏 (用于前端展示心形图标)
     */
    @Override
    public boolean isFava(Long userId, Long goodsId, String type) {
        return favaMapper.countFava(userId, goodsId, type) > 0;
    }
}