package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotFeedbackVO;

import java.util.List;

/**
 * 首页热门用户反馈 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageFeedbackService {

    /**
     * 查询首页热门用户反馈
     *
     * @param limit 返回数量
     * @return 热门反馈列表
     */
    List<HotFeedbackVO> getHotFeedback(int limit);
}
