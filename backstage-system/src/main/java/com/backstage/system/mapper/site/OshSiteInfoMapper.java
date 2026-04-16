package com.backstage.system.mapper.site;

import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.domain.site.OshSiteMaintainer;
import com.backstage.system.domain.site.OshSiteUsage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 内部网站信息 Mapper 接口
 *
 * @author backstage
 */
@Mapper
public interface OshSiteInfoMapper extends BaseMapper<OshSiteInfo> {

  /**
   * 新增网站使用记录
   *
   * @param oshSiteUsage 网站使用记录
   * @return 结果
   */
  int insertUsage(@Param("oshSiteUsage") OshSiteUsage oshSiteUsage);

  /**
   * 查询网站负责人列表
   *
   * @param siteId 网站ID
   * @return 负责人列表
   */
  List<OshSiteMaintainer> selectResponsibleBySiteId(@Param("siteId") Long siteId);

  /**
   * 新增网站负责人
   *
   * @param responsible 负责人信息
   * @return 结果
   */
  int insertResponsible(OshSiteMaintainer responsible);

  /**
   * 删除网站负责人
   *
   * @param id 负责人记录ID
   * @return 结果
   */
  int deleteResponsibleById(@Param("id") Long id);

  /**
   * 批量删除网站负责人
   *
   * @param ids 负责人记录ID数组
   * @return 结果
   */
  int deleteResponsibleByIds(@Param("ids") Long[] ids);

  /**
   * 删除网站的所有负责人
   *
   * @param siteId 网站ID
   * @return 结果
   */
  int deleteResponsibleBySiteId(@Param("siteId") Long siteId);

  List<OshSiteMaintainer> selectMaintainersBySiteIds(@Param("siteIds") Collection<Long> siteIds);
}
