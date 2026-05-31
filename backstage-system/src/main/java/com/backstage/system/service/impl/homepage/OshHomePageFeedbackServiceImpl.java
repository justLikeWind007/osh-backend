package com.backstage.system.service.impl.homepage;

import com.backstage.system.domain.homepage.vo.HotFeedbackVO;
import com.backstage.system.mapper.homepage.OshHomePageFeedbackMapper;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页热门用户反馈 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageFeedbackServiceImpl implements IOshHomePageFeedbackService {

    @Autowired
    private OshHomePageFeedbackMapper homePageFeedbackMapper;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotFeedbackVO> getHotFeedback(int limit) {
        List<HotFeedbackVO> list = homePageFeedbackMapper.selectHotFeedback(limit);
        for (HotFeedbackVO vo : list) {
            vo.setDetailUrl(modulePathService.getDetailPath("feedback", vo.getId()));
        }
        return list;
    }
}
