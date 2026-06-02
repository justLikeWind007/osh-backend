package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotGroupVO;

import java.util.List;

/**
 * 首页热门拼团 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageGroupService {

    /**
     * 查询首页热门拼团活动
     *
     * @param limit 返回数量
     * @return 热门拼团列表
     */
    List<HotGroupVO> getHotGroup(int limit);
}
