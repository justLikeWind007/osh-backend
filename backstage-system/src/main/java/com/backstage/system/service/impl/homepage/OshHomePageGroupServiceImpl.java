package com.backstage.system.service.impl.homepage;

import com.backstage.system.domain.homepage.vo.HotGroupVO;
import com.backstage.system.mapper.homepage.OshHomePageGroupMapper;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页热门拼团 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageGroupServiceImpl implements IOshHomePageGroupService {

    @Autowired
    private OshHomePageGroupMapper homePageGroupMapper;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotGroupVO> getHotGroup(int limit) {
        List<HotGroupVO> list = homePageGroupMapper.selectHotGroup(limit);
        for (HotGroupVO vo : list) {
            // 拼团商品跳转到课程详情页（group 模块 key 映射到 /detail/course/）
            vo.setDetailUrl(modulePathService.getDetailPath("group", vo.getGoodsId()));
        }
        return list;
    }
}
