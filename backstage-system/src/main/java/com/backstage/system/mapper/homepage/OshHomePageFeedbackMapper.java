package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotFeedbackVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门用户反馈 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageFeedbackMapper {

    /**
     * 查询热门用户反馈（按热度分排序）
     */
    List<HotFeedbackVO> selectHotFeedback(@Param("limit") int limit);
}
