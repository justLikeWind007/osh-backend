package com.backstage.system.service.impl.homepage;

import com.backstage.system.domain.homepage.vo.HotSeckillVO;
import com.backstage.system.mapper.homepage.OshHomePageSeckillMapper;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageSeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页热门秒杀 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageSeckillServiceImpl implements IOshHomePageSeckillService {

    @Autowired
    private OshHomePageSeckillMapper homePageSeckillMapper;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotSeckillVO> getHotSeckill(int limit) {
        List<HotSeckillVO> list = homePageSeckillMapper.selectHotSeckill(limit);
        for (HotSeckillVO vo : list) {
            if (vo.getActivityId() != null) {
                // 秒杀卡片统一跳转到秒杀详情页，路径前缀由 FrontPathConstants 统一维护
                vo.setDetailUrl(modulePathService.getDetailPath("seckill", vo.getActivityId()));
            } else {
                // activityId 为空时兜底到秒杀列表页
                vo.setDetailUrl(modulePathService.getListPath("seckill"));
            }
        }
        return list;
    }
}
