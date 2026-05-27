package com.backstage.system.service.info_gap;

import com.backstage.system.domain.dto.info_gap.InfoGapAnnoRespDTO;

import java.util.List;

public interface InfoGapAnnoService {

    // 展示系统公告栏内容
    List<InfoGapAnnoRespDTO> listSystemNotices();

    // 展示用户通知栏内容
    List<InfoGapAnnoRespDTO> listUserNotices();
}
