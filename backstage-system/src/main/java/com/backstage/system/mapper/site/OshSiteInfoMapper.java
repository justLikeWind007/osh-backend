package com.backstage.system.mapper.site;

import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.domain.site.OshSiteUsage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
