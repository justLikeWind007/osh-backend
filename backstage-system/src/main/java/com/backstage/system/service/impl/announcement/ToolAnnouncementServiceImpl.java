package com.backstage.system.service.impl.announcement;

import com.backstage.system.domain.vo.announcement.ToolAnnouncementVO;
import com.backstage.system.mapper.announcement.OshAnnouncementMapper;
import com.backstage.system.service.announcement.IToolAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ToolAnnouncementServiceImpl implements IToolAnnouncementService {

    @Autowired
    private OshAnnouncementMapper oshAnnouncementMapper;

    @Override
    public List<ToolAnnouncementVO> listLatestToolAnnouncements() {
        List<ToolAnnouncementVO> list = oshAnnouncementMapper.selectLatestToolAnnouncements();
        return list == null ? Collections.emptyList() : list;
    }
}
