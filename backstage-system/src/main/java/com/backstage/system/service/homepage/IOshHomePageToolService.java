package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotToolVO;

import java.util.List;

/**
 * 首页热门工具 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageToolService {

    /**
     * 查询首页热门工具
     *
     * @param limit 返回数量
     * @return 热门工具列表
     */
    List<HotToolVO> getHotTools(int limit);
}
