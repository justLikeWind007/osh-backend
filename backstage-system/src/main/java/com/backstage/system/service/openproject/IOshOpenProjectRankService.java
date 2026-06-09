package com.backstage.system.service.openproject;

import com.backstage.system.domain.openproject.vo.OpenProjectRankVO;

import java.util.List;

public interface IOshOpenProjectRankService {

    /**
     * 获取排行榜
     *
     * @param rankType 排行类型：star / fork
     * @param period   统计周期：7 / 30（天）
     * @param topN     返回前 N 名
     */
    List<OpenProjectRankVO> getRank(String rankType, int period, int topN);

    /**
     * 保存今日快照（由定时任务调用）
     */
    void saveTodaySnapshot();
}
