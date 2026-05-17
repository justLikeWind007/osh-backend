package com.backstage.system.service.info_gap;

import com.backstage.system.domain.dto.info_gap.InfoGapCreateDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapSearchReqDTO;
import com.backstage.system.domain.info_gap.OshInfoGap;
import com.backstage.system.domain.vo.info_gap.InfoGapVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface InfoGapService {
    
    // 分页查询逻辑，包含关联用户信息和排序
    List<InfoGapVO> getInfoGapList(Integer pageNum, Integer pageSize, String type, Long currentUserId);

    // 包含风控校验的发布逻辑
    void createInfoGap(InfoGapCreateDTO dto, Long userId);

    // 包含幂等校验和原子更新的评价逻辑
    void vote(Long userId, Long infoId, Integer type);

   void toggleFollow(Long currentUserId, Long targetUserId);

    // 精品推荐列表
    List<InfoGapVO> recommend();

    // 收藏信息差
    void infoGapCollectAdd(Long userId, String username, Long infoGapId);

    // 取消收藏信息差
    void infoGapCollectRemove(Long userId, String username, Long infoGapId);

    // 统计点击观看信息差的次数
    void viewCount(Long infoGapId);

    // 搜索查询信息差
    List<InfoGapVO> searchInfoGap(InfoGapSearchReqDTO infoGapSearchReqDTO);
}