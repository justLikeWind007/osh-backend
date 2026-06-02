package com.backstage.system.service.impl.homepage;

import com.backstage.system.domain.homepage.vo.HotQaVO;
import com.backstage.system.mapper.homepage.OshHomePageQaMapper;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageQaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页热门答疑 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageQaServiceImpl implements IOshHomePageQaService {

    @Autowired
    private OshHomePageQaMapper homePageQaMapper;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotQaVO> getHotQa(int limit) {
        List<HotQaVO> list = homePageQaMapper.selectHotQa(limit);
        for (HotQaVO vo : list) {
            vo.setDetailUrl(modulePathService.getDetailPath("qa", vo.getId()));
        }
        return list;
    }
}
