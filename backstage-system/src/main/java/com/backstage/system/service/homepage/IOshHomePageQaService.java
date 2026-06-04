package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotQaVO;

import java.util.List;

/**
 * 首页热门答疑 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageQaService {

    /**
     * 查询首页热门答疑
     *
     * @param limit 返回数量
     * @return 热门答疑列表
     */
    List<HotQaVO> getHotQa(int limit);
}
