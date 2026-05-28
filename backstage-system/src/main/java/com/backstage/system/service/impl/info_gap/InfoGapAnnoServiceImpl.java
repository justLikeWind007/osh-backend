package com.backstage.system.service.impl.info_gap;

import com.backstage.common.enums.AnnouncementChannelEnum;
import com.backstage.system.domain.dto.info_gap.InfoGapAnnoRespDTO;
import com.backstage.system.mapper.info_gap.OshInfoGapAnnouncementMapper;
import com.backstage.system.service.info_gap.InfoGapAnnoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class InfoGapAnnoServiceImpl implements InfoGapAnnoService {

    private static final String SYSTEM_OPERATOR = "system";

    @Resource
    private OshInfoGapAnnouncementMapper oshInfoGapAnnouncementMapper;

    @Override
    public List<InfoGapAnnoRespDTO> listSystemNotices() {
        return Collections.emptyList();
    }

    @Override
    public List<InfoGapAnnoRespDTO> listUserNotices() {
        List<InfoGapAnnoRespDTO> list =
                oshInfoGapAnnouncementMapper.selectLatestInfoGapAnnouncementsByChannel(
                        AnnouncementChannelEnum.USER_NOTICE.getCode()
                );
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public void publishUserNotice(Long infoGapId, String title, String userName, String no) {
        if (infoGapId == null) {
            return;
        }

        String safeUserName = (userName == null || userName.trim().isEmpty()) ? "有用户" : userName.trim();
        String safeTitle = (title == null || title.trim().isEmpty()) ? "未命名信息差" : title.trim();
        String announcementTitle = safeUserName + " 发布了新信息差「" + safeTitle + "」";
        String link = buildLink(infoGapId, no);

        oshInfoGapAnnouncementMapper.insertInfoGapAnnouncement(
                announcementTitle,
                link,
                AnnouncementChannelEnum.USER_NOTICE.getCode(),
                SYSTEM_OPERATOR
        );
    }

    private String buildLink(Long infoGapId, String no) {
        if (no != null && !no.trim().isEmpty()) {
            return "/info_gap/detail/" + no.trim();
        }
        return "/info_gap/detail/" + infoGapId;
    }
}
