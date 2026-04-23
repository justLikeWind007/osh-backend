package com.backstage.system.mapper.website;

import com.backstage.system.domain.website.OshWebsiteTagRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 网站标签关联 Mapper
 */
public interface OshWebsiteTagRelMapper extends BaseMapper<OshWebsiteTagRel> {

    @Insert({
            "<script>",
            "INSERT INTO osh_website_tag_rel (website_id, tag_id, delete_flag) VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.websiteId}, #{item.tagId}, #{item.deleteFlag})",
            "</foreach>",
            "</script>"
    })
    int batchInsertRel(@Param("list") List<OshWebsiteTagRel> list);

    @Update({
            "<script>",
            "UPDATE osh_website_tag_rel",
            "SET delete_flag = 1",
            "WHERE delete_flag = 0",
            "AND website_id IN",
            "<foreach collection='websiteIds' item='websiteId' open='(' separator=',' close=')'>",
            "#{websiteId}",
            "</foreach>",
            "</script>"
    })
    int deleteByWebsiteIds(@Param("websiteIds") List<Integer> websiteIds);
}
