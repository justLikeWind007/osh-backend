package com.backstage.system.service.tool;

import com.backstage.system.domain.vo.tool.ToolAnnouncementVO;

import java.util.List;

public interface IToolAnnouncementService {

    List<ToolAnnouncementVO> listLatestSystemNotices();

    List<ToolAnnouncementVO> listLatestUserNotices();
}
