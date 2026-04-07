package com.backstage.system.mapper.site;

import com.backstage.system.domain.site.OshSiteTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 内部网站标签 Mapper 接口
 *
 * @author backstage
 */
public interface OshSiteTagsMapper extends BaseMapper<OshSiteTag> {

  @MapKey("id")
  List<OshSiteTag> countTagUsage(@Param("tagIds") Collection<Long> siteIds);

  List<OshSiteTag> selectSiteTagsById(@Param("siteIds") Collection<Long> siteIds);

  int deleteAllSiteTags(@Param("siteIds") Collection<Long> siteIds, @Param("userId") Long userId);

  int saveSiteTags(@Param("siteId") Long siteId, @Param("tagIds") Collection<Long> tagIds, @Param("userId") Long userId);
}
