package com.backstage.system.service.announcement;

import com.backstage.system.domain.vo.announcement.ToolAnnouncementVO;

import java.util.List;

public interface IToolAnnouncementService {

    List<ToolAnnouncementVO> listLatestToolAnnouncements();
}
