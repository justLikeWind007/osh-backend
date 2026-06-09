package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotToolVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门工具 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageToolMapper {

    /**
     * 查询热门工具（按累计使用次数排序）
     */
    List<HotToolVO> selectHotTools(@Param("limit") int limit);
}
