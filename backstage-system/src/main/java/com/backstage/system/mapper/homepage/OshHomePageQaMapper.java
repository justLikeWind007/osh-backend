package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotQaVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门答疑 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageQaMapper {

    /**
     * 查询热门答疑（基于热度公式排序）
     */
    List<HotQaVO> selectHotQa(@Param("limit") int limit);
}
