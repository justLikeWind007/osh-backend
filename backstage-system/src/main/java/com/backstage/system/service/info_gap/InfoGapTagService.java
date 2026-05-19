package com.backstage.system.service.info_gap;

import com.backstage.system.domain.dto.info_gap.InfoGapTagListRespDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapUpdateReqDTO;

import java.util.List;

public interface InfoGapTagService {
    // 获取所有标签
    List<InfoGapTagListRespDTO> getTagList();

    void addTag(String tagName);

    void deleteTag(Long id);
}
