package com.backstage.system.service.site.impl;

import com.backstage.system.domain.site.OshSiteTag;
import com.backstage.system.mapper.site.OshSiteTagsMapper;
import com.backstage.system.service.site.IOshSiteTagsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 内部网站标签 Service 业务层处理
 *
 * @author backstage
 */
@Service
public class OshSiteTagsServiceImpl extends ServiceImpl<OshSiteTagsMapper, OshSiteTag> implements IOshSiteTagsService {
  /**
   * 查询网站的标签列表
   *
   * @param siteId 网站 ID
   * @return 标签列表
   */
  @Override
  public List<OshSiteTag> getTagsBySiteId(Long siteId) {
    return baseMapper.selectSiteTagsById(Collections.singletonList(siteId));
  }

  /**
   * 查询所有标签及其使用状态
   *
   * @return 标签信息列表（包含使用次数）
   */
  @Override
  public List<OshSiteTag> getAllTagsWithUsage() {
    return getAllTagsWithUsage(Collections.emptyList());
  }

  @Override
  public List<OshSiteTag> getAllTag(Collection<Long> siteIds) {
    if (CollectionUtils.isEmpty(siteIds)) {
      return Collections.emptyList();
    }
    return baseMapper.selectSiteTagsById(siteIds);
  }

  @Override
  public List<OshSiteTag> getAllTagsWithUsage(Collection<Long> siteIds) {
    List<OshSiteTag> allTags = this.list(new LambdaQueryWrapper<OshSiteTag>()
            .select(OshSiteTag::getTagName, OshSiteTag::getId)
            .in(!CollectionUtils.isEmpty(siteIds), OshSiteTag::getSiteId, siteIds)
            .eq(OshSiteTag::getIsDeleted, 0));
    // 统计每个标签的使用次数
    Set<Long> tagIds = allTags.stream().map(OshSiteTag::getId).collect(Collectors.toSet());
    if (!CollectionUtils.isEmpty(tagIds)) {
      Map<Long, Long> usageCount = baseMapper.countTagUsage(tagIds)
              .stream().collect(Collectors.toMap(OshSiteTag::getId, OshSiteTag::getUsageCount, (k1, k2) -> k1));
      for (OshSiteTag allTag : allTags) {
        allTag.setUsageCount(usageCount.getOrDefault(allTag.getId(), 0L));
      }
    }
    return allTags;
  }

  /**
   * 保存网站的标签
   *
   * @param siteId 网站 ID
   * @param tags   标签列表
   * @param userId 用户 ID
   * @return 结果
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public int saveSiteTags(Long siteId, List<OshSiteTag> tags, Long userId) {
    if (CollectionUtils.isEmpty(tags)) {
      return 0;
    }
    deleteSiteTags(Collections.singletonList(siteId), userId);
    Set<Long> tagIds = tags.stream().map(OshSiteTag::getId).collect(Collectors.toSet());
    return saveSiteTags(siteId, tagIds, userId);
  }

  @Override
  public int deleteSiteTags(Collection<Long> siteIds, Long userId) {
    return baseMapper.deleteAllSiteTags(siteIds, userId);
  }

  @Override
  public int saveSiteTags(Long siteId, Collection<Long> tagIds, Long userId) {
    return baseMapper.saveSiteTags(siteId, tagIds, userId);
  }
}
