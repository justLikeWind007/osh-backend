package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotBookVO;

import java.util.List;

/**
 * 首页热门电子书 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageBookService {

    /**
     * 查询首页热门电子书
     *
     * @param limit 返回数量
     * @return 热门电子书列表（含标签和跳转链接）
     */
    List<HotBookVO> getHotBooks(int limit);
}
