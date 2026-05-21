package com.backstage.system.mapper.tool;

import com.backstage.system.domain.vo.tool.ToolAnnouncementVO;
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
}
