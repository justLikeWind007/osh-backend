package com.backstage.system.service.impl;

import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.domain.site.OshSiteUsage;
import com.backstage.system.mapper.site.OshSiteInfoMapper;
import com.backstage.system.service.IOshSiteInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 内部网站信息 Service 业务层处理
 *
 * @author backstage
 */
@Service
public class OshSiteInfoServiceImpl extends ServiceImpl<OshSiteInfoMapper, OshSiteInfo> implements IOshSiteInfoService {

    @Autowired
    private OshSiteInfoMapper oshSiteInfoMapper;

    /**
     * 新增网站使用记录
     * 
     * @param siteInfo 网站信息
     * @return 结果
     */
    @Override
    public int insertUsage(OshSiteInfo siteInfo) {
        OshSiteUsage oshSiteUsage = new OshSiteUsage();
        oshSiteUsage.setSiteId(siteInfo.getId());
        oshSiteUsage.setUserId(1L);
        oshSiteUsage.setCreatedBy("1");
        oshSiteUsage.setCreationTime(new Date());
        oshSiteUsage.setUpdateTime(new Date());
        return oshSiteInfoMapper.insertUsage(oshSiteUsage);
    }
}
