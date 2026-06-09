package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotOpenProjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门开源项目 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageOpenProjectMapper {

    /**
     * 查询热门开源项目（按 star 数 + 点击数排序）
     */
    List<HotOpenProjectVO> selectHotOpenProjects(@Param("limit") int limit);

    /**
     * 根据项目ID批量查询标签
     */
    List<HotOpenProjectVO> selectTagsByProjectIds(@Param("projectIds") List<Long> projectIds);
}
