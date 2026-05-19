package com.backstage.system.mapper.announcement;

import com.backstage.system.domain.vo.announcement.ToolAnnouncementVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OshAnnouncementMapper {

    @Select("SELECT id, title, link, create_time AS createTime " +
            "FROM osh_announcement " +
            "WHERE delete_flag = 0 AND status = 0 AND resource_type = 'tool' " +
            "ORDER BY create_time DESC " +
            "LIMIT 5")
    List<ToolAnnouncementVO> selectLatestToolAnnouncements();
}
