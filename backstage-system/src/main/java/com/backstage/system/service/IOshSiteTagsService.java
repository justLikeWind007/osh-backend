package com.backstage.system.service;

import com.backstage.system.domain.site.OshSiteTags;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 内部网站标签 Service 接口
 * 
 * @author backstage
 */
public interface IOshSiteTagsService extends IService<OshSiteTags> {

    /**
     * 查询网站的所有标签（去重）
     * 
     * @return 标签列表
     */
    List<String> getAllTags();

    /**
     * 查询网站的标签列表
     * 
     * @param siteId 网站 ID
     * @return 标签列表
     */
    List<String> getTagsBySiteId(Long siteId);

    /**
     * 查询所有标签及其使用状态
     * 
     * @return 标签信息列表（包含使用次数）
     */
    List<Map<String, Object>> getAllTagsWithUsage();

    /**
     * 保存网站的标签
     * 
     * @param siteId 网站 ID
     * @param tags 标签列表
     * @param userId 用户 ID
     * @return 结果
     */
    int saveSiteTags(Long siteId, List<String> tags, Long userId);

}
