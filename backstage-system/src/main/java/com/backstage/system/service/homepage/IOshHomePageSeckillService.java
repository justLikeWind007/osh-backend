package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotSeckillVO;

import java.util.List;

/**
 * 首页热门秒杀 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageSeckillService {

    /**
     * 查询首页热门秒杀商品（当前进行中活动）
     *
     * @param limit 返回数量
     * @return 热门秒杀列表
     */
    List<HotSeckillVO> getHotSeckill(int limit);
}
