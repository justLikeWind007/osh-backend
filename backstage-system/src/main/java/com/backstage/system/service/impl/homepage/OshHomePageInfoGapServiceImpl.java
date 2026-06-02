package com.backstage.system.service.impl.homepage;

import com.backstage.system.domain.homepage.vo.HotInfoGapVO;
import com.backstage.system.mapper.homepage.OshHomePageInfoGapMapper;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageInfoGapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页热门信息差 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageInfoGapServiceImpl implements IOshHomePageInfoGapService {

    @Autowired
    private OshHomePageInfoGapMapper homePageInfoGapMapper;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotInfoGapVO> getHotInfoGap(int limit) {
        List<HotInfoGapVO> list = homePageInfoGapMapper.selectHotInfoGap(limit);
        for (HotInfoGapVO vo : list) {
            vo.setDetailUrl(modulePathService.getDetailPath("info_gap", vo.getId()));
        }
        return list;
    }
}
