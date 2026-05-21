package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotBookVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门电子书 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageBookMapper {

    /**
     * 查询热门电子书（基于热度公式排序）
     *
     * @param limit 返回数量
     * @return 热门电子书列表
     */
    List<HotBookVO> selectHotBooks(@Param("limit") int limit);

    /**
     * 根据电子书ID批量查询关联标签
     *
     * @param bookIds 电子书ID列表
     * @return 电子书-标签关联列表
     */
    List<HotBookVO> selectTagsByBookIds(@Param("bookIds") List<Long> bookIds);
}
