package com.backstage.system.mapper.openproject;

import com.backstage.system.domain.vo.tool.ToolAnnouncementVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OshOpenProjectAnnouncementMapper {

    @Select("SELECT id, title, link, create_time AS createTime " +
            "FROM osh_announcement " +
            "WHERE delete_flag = 0 AND status = 0 AND resource_type = 'serial' AND channel = 0 " +
            "ORDER BY create_time DESC " +
            "LIMIT 5")
    List<ToolAnnouncementVO> selectLatestOpenProjectAnnouncements();
}
