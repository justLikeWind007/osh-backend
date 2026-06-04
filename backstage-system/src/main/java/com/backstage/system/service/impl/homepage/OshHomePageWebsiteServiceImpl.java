package com.backstage.system.service.impl.homepage;

import com.backstage.system.domain.homepage.vo.HotWebsiteVO;
import com.backstage.system.mapper.homepage.OshHomePageWebsiteMapper;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageWebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页热门实用网站 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageWebsiteServiceImpl implements IOshHomePageWebsiteService {

    @Autowired
    private OshHomePageWebsiteMapper homePageWebsiteMapper;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotWebsiteVO> getHotWebsites(int limit) {
        List<HotWebsiteVO> list = homePageWebsiteMapper.selectHotWebsites(limit);
        for (HotWebsiteVO vo : list) {
            vo.setDetailUrl(modulePathService.getListPath("usefull"));
        }
        return list;
    }
}
