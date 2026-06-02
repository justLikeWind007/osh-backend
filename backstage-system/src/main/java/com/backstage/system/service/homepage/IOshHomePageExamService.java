package com.backstage.system.service.homepage;

import com.backstage.system.domain.homepage.vo.HotExamVO;

import java.util.List;

/**
 * 首页热门考试 Service 接口
 *
 * @author jayTatum
 */
public interface IOshHomePageExamService {

    /**
     * 查询首页热门考试
     *
     * @param limit 返回数量
     * @return 热门考试列表（含标签和跳转链接）
     */
    List<HotExamVO> getHotExams(int limit);
}
