package com.backstage.system.service.info_gap;

import com.backstage.common.core.domain.R;

public interface InfoGapCollectService {

    // 收藏/取消收藏信息差
    R<String> collectInfoGap(Long userId, Long infoGapId);
}
