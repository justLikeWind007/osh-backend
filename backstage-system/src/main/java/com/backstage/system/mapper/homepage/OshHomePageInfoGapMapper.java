package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotInfoGapVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门信息差 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageInfoGapMapper {

    /**
     * 查询热门信息差（按好评数 + 浏览数排序）
     */
    List<HotInfoGapVO> selectHotInfoGap(@Param("limit") int limit);
}
