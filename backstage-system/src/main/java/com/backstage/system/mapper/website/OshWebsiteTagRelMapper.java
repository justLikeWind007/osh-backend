package com.backstage.system.mapper.website;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backstage.system.domain.website.OshWebsiteTagRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description 针对表【osh_website_tag_rel(网站与标签关联表)】的数据库操作Mapper
 */
public interface OshWebsiteTagRelMapper extends BaseMapper<OshWebsiteTagRel> {

    /**
     * 批量插入网站标签关联关系
     * @param list 关联关系列表
     * @return 插入的行数
     */
    int batchInsertRel(@Param("list") List<OshWebsiteTagRel> list);

    /**
     * 根据单个网站ID软删除关联关系（用于编辑网站时重建标签）
     *
     * @param websiteId 网站 ID
     * @return 删除的行数
     */
    int deleteByWebsiteId(@Param("websiteId") Long websiteId);

    /**
     * 根据网站ID批量软删除关联关系
     * @param websiteIds 网站ID列表
     * @return 删除的行数
     */
    int deleteByWebsiteIds(@Param("websiteIds") List<Integer> websiteIds);
}
