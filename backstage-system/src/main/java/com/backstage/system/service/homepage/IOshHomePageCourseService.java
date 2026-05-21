package com.backstage.system.service.homepage;


import com.backstage.system.domain.homepage.vo.HotCourseVO;

import java.util.List;

/**
 * 首页热门课程 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageCourseService {

    /**
     * 查询首页热门课程
     *
     * @param limit 返回数量
     * @return 热门课程列表（含标签和跳转链接）
     */
    List<HotCourseVO> getHotCourses(int limit);
}
