package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotOpenProjectVO;

import java.util.List;

/**
 * 首页热门开源项目 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageOpenProjectService {

    /**
     * 查询首页热门开源项目
     *
     * @param limit 返回数量
     * @return 热门开源项目列表（含标签）
     */
    List<HotOpenProjectVO> getHotOpenProjects(int limit);
}
