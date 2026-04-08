package com.backstage.system.mapper.website;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backstage.system.domain.website.OshWebsiteTag;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

/**
* @author 24333
* @description 针对表【osh_website_tag(实用网站标签表)】的数据库操作Mapper
* @createDate 2026-03-31 14:31:36
* @Entity generator.domain.OshWebsiteTag
*/
public interface OshWebsiteTagMapper extends BaseMapper<OshWebsiteTag> {
    @Insert("insert into osh_website_tag (id, tag_name,delete_flag) values (#{id}, #{tagName},#{deleteFlag})")
    int insertWebsiteTag(OshWebsiteTag tag);
    /**
     * 根据网站id批量删除网站标签信息
     * @param websiteIds 要删除的网站 ID 列表
     */
    int batchDeleteWebsiteTag(List<Integer> websiteIds);
}




