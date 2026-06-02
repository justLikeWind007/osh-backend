package com.backstage.system.service.impl.homepage;

import com.backstage.system.domain.homepage.vo.HotToolVO;
import com.backstage.system.mapper.homepage.OshHomePageToolMapper;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页热门工具 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageToolServiceImpl implements IOshHomePageToolService {

    @Autowired
    private OshHomePageToolMapper homePageToolMapper;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotToolVO> getHotTools(int limit) {
        List<HotToolVO> list = homePageToolMapper.selectHotTools(limit);
        for (HotToolVO vo : list) {
            vo.setDetailUrl(modulePathService.getDetailPath("tool", vo.getId()));
        }
        return list;
    }
}
