package com.backstage.system.service.site;

import com.backstage.system.domain.site.OshSiteTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * 内部网站标签 Service 接口
 *
 * @author backstage
 */
public interface IOshSiteTagsService extends IService<OshSiteTag> {

  /**
   * 查询网站的标签列表
   *
   * @param siteId 网站 ID
   * @return 标签列表
   */
  List<OshSiteTag> getTagsBySiteId(Long siteId);

  /**
   * 查询所有标签及其使用状态
   *
   * @return 标签信息列表（包含使用次数）
   */
  List<OshSiteTag> getAllTagsWithUsage();

  List<OshSiteTag> getAllTag(Collection<Long> siteIds);

  List<OshSiteTag> getAllTagsWithUsage(Collection<Long> siteIds);

  /**
   * 保存网站的标签
   *
   * @param siteId 网站 ID
   * @param tags   标签列表
   * @param userId 用户 ID
   * @return 结果
   */
  int saveSiteTags(Long siteId, List<OshSiteTag> tags, Long userId);

  /**
   * 删除网站标签
   *
   * @param siteIds 网站 ID列表
   * @param userId  用户 ID
   * @return 删除结果
   */
  int deleteSiteTags(Collection<Long> siteIds, Long userId);

  int saveSiteTags(Long siteId, Collection<Long> tagIds, Long userId);
}
