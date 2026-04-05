package com.backstage.system.service;

import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.domain.site.OshSiteUsage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 内部网站信息 Service 接口
 *
 * @author backstage
 */
public interface IOshSiteInfoService extends IService<OshSiteInfo> {

    /**
     * 新增网站使用记录
     * 
     * @param siteInfo 网站信息
     * @return 结果
     */
    int insertUsage(OshSiteInfo siteInfo);

}
