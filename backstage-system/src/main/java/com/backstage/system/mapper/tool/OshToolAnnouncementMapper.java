package com.backstage.system.mapper.tool;

import com.backstage.system.domain.vo.tool.ToolAnnouncementVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OshToolAnnouncementMapper {

    @Select("SELECT id, title, link, create_time AS createTime " +
            "FROM osh_announcement " +
            "WHERE delete_flag = 0 AND status = 0 AND resource_type = 'tool' AND channel = #{channel} " +
            "ORDER BY create_time DESC " +
            "LIMIT 5")
    List<ToolAnnouncementVO> selectLatestToolAnnouncementsByChannel(@Param("channel") Integer channel);

    @Insert("INSERT INTO osh_announcement (" +
            "title, link, resource_type, channel, status, delete_flag, create_by, create_time, update_by, update_time" +
            ") VALUES (" +
            "#{title}, #{link}, 'tool', #{channel}, 0, 0, #{operator}, NOW(), #{operator}, NOW()" +
            ")")
    int insertToolAnnouncement(@Param("title") String title,
                               @Param("link") String link,
                               @Param("channel") Integer channel,
                               @Param("operator") String operator);
}
