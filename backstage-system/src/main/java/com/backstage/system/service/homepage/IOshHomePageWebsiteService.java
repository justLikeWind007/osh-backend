package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotWebsiteVO;

import java.util.List;

/**
 * 首页热门实用网站 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageWebsiteService {

    /**
     * 查询首页热门实用网站
     *
     * @param limit 返回数量
     * @return 热门网站列表
     */
    List<HotWebsiteVO> getHotWebsites(int limit);
}
