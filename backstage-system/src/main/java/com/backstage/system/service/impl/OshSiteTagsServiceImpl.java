package com.backstage.system.service.impl;

import com.backstage.system.domain.site.OshSiteTags;
import com.backstage.system.mapper.OshSiteTagsMapper;
import com.backstage.system.service.IOshSiteTagsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内部网站标签 Service 业务层处理
 * 
 * @author backstage
 */
@Service
public class OshSiteTagsServiceImpl extends ServiceImpl<OshSiteTagsMapper, OshSiteTags> implements IOshSiteTagsService {

    /**
     * 查询网站的所有标签（去重）
     * 
     * @return 标签列表
     */
    @Override
    public List<String> getAllTags() {
        LambdaQueryWrapper<OshSiteTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(OshSiteTags::getTagName)
                .eq(OshSiteTags::getIsDeleted, 0)
                .groupBy(OshSiteTags::getTagName);
        
        List<OshSiteTags> tags = this.list(queryWrapper);
        return tags.stream()
                .map(OshSiteTags::getTagName)
                .collect(Collectors.toList());
    }

    /**
     * 查询网站的标签列表
     * 
     * @param siteId 网站 ID
     * @return 标签列表
     */
    @Override
    public List<String> getTagsBySiteId(Long siteId) {
        LambdaQueryWrapper<OshSiteTags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(OshSiteTags::getTagName)
                .eq(OshSiteTags::getSiteId, siteId)
                .eq(OshSiteTags::getIsDeleted, 0);
        
        List<OshSiteTags> tags = this.list(queryWrapper);
        return tags.stream()
                .map(OshSiteTags::getTagName)
                .collect(Collectors.toList());
    }

    /**
     * 查询所有标签及其使用状态
     * 
     * @return 标签信息列表（包含使用次数）
     */
    @Override
    public List<Map<String, Object>> getAllTagsWithUsage() {
        // 手动统计每个标签的使用次数
        Map<String, Long> tagUsageMap = new HashMap<>();
        List<OshSiteTags> allTags = this.list(new LambdaQueryWrapper<OshSiteTags>()
            .select(OshSiteTags::getTagName, OshSiteTags::getSiteId)
            .eq(OshSiteTags::getIsDeleted, 0));
        
        for (OshSiteTags tag : allTags) {
            tagUsageMap.put(tag.getTagName(), 
                tagUsageMap.getOrDefault(tag.getTagName(), 0L) + 1);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Long> entry : tagUsageMap.entrySet()) {
            Map<String, Object> tagInfo = new HashMap<>();
            tagInfo.put("id", ++index);
            tagInfo.put("tagName", entry.getKey());
            tagInfo.put("usageCount", entry.getValue());
            result.add(tagInfo);
        }
        
        return result;
    }

    /**
     * 保存网站的标签
     * 
     * @param siteId 网站 ID
     * @param tags 标签列表
     * @param userId 用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveSiteTags(Long siteId, List<String> tags, Long userId) {
        // 删除旧的标签
        LambdaQueryWrapper<OshSiteTags> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(OshSiteTags::getSiteId, siteId);
        this.remove(deleteWrapper);

        // 添加新的标签
        if (tags != null && !tags.isEmpty()) {
            List<OshSiteTags> tagList = new ArrayList<>();
            Date now = new Date();
            
            for (String tagName : tags) {
                if (tagName != null && !tagName.trim().isEmpty()) {
                    OshSiteTags siteTag = new OshSiteTags();
                    siteTag.setSiteId(siteId);
                    siteTag.setTagName(tagName.trim());
                    siteTag.setCreatedBy(userId);
                    siteTag.setCreationTime(now);
                    siteTag.setUpdateBy(userId);
                    siteTag.setUpdateTime(now);
                    siteTag.setIsDeleted(0);
                    tagList.add(siteTag);
                }
            }
            
            if (!tagList.isEmpty()) {
                return this.saveBatch(tagList) ? tagList.size() : 0;
            }
        }
        
        return 0;
    }
}
