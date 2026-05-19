package com.backstage.system.mapper.homepage;


import com.backstage.system.domain.homepage.OshHomePageCarousel;

import java.util.List;

/**
 * 首页轮播图卡片的Mapper接口
 *
 * @author jayTatum
 *
 */
public interface OshHomePageCarouselMapper {
    /**
     * 查询轮播图卡片
     */

    public OshHomePageCarousel selectCarouselById(long id);

    /**
     * 新增轮播图卡片
     */
    public int insertCarousel(OshHomePageCarousel carousel);

    /**
     * 查询所有可见的轮播图卡片（前台展示用）
     */
    public List<OshHomePageCarousel>  selectVisibleCarouselList();

    /**
     * 查询轮播图卡片列表（后台管理）
     */
    public List<OshHomePageCarousel> selectCarouselList(OshHomePageCarousel carousel);

    /**
     * 查询所有未删除的卡片ID
     */
    public List<Long> selectAllCarouselIds();

    /**
     * 逻辑删除轮播图卡片
     */
    public int deleteCarouselById(Long id);

    /**
     * 批量逻辑删除轮播图卡片
     */
    public int deleteCarouselByIds(Long[] ids);

    /**
     * 修改轮播图卡片
     */
    public int updateCarousel(OshHomePageCarousel carousel);



}
