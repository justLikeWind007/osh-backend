package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotInfoGapVO;

import java.util.List;

/**
 * 首页热门信息差 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageInfoGapService {

    /**
     * 查询首页热门信息差
     *
     * @param limit 返回数量
     * @return 热门信息差列表
     */
    List<HotInfoGapVO> getHotInfoGap(int limit);
}
