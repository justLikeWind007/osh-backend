package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotWebsiteVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门实用网站 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageWebsiteMapper {

    /**
     * 查询热门实用网站（按点击数 + 好评数排序）
     */
    List<HotWebsiteVO> selectHotWebsites(@Param("limit") int limit);
}
