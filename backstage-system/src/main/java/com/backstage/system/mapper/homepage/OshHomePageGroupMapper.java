package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotGroupVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门拼团 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageGroupMapper {

    /**
     * 查询热门拼团活动（关联课程信息）
     */
    List<HotGroupVO> selectHotGroup(@Param("limit") int limit);
}
