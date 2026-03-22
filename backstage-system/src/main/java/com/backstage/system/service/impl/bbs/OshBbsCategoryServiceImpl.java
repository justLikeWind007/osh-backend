package com.backstage.system.service.impl.bbs;

import com.backstage.system.domain.bbs.OshBbsCategory;
import com.backstage.system.domain.vo.bbs.BbsSummaryVo;
import com.backstage.system.mapper.bbs.OshBbsCategoryMapper;
import com.backstage.system.service.bbs.IOshBbsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OshBbsCategoryServiceImpl implements IOshBbsCategoryService {

    @Autowired
    private OshBbsCategoryMapper categoryMapper;

    @Override
    public BbsSummaryVo getBbsSummary(OshBbsCategory category) {
        BbsSummaryVo vo = new BbsSummaryVo();
        
        // 1. 列表数据
        List<OshBbsCategory> list = categoryMapper.selectCategoryList(category);
        vo.setRows(list);
        vo.setCount((long) list.size());
        
        // 2. 统计数据
        vo.setUserCount(categoryMapper.countTotalUsers());
        vo.setPostCount(categoryMapper.countTotalPosts());
        
        return vo;
    }
}