package com.backstage.system.service.bbs;

import com.backstage.system.domain.bbs.OshBbsCategory;
import com.backstage.system.domain.vo.bbs.BbsSummaryVo;

public interface IOshBbsCategoryService {
    public BbsSummaryVo getBbsSummary(OshBbsCategory category);
}