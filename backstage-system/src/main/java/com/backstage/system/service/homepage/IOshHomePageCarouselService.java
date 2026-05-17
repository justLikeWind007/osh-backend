package com.backstage.system.service.homepage;


import com.backstage.system.domain.homepage.OshHomePageCarousel;

import java.util.List;

/**
 *
 * 首页轮播图卡片service接口
 *
 * @author jayTatum
 *
 */
public interface IOshHomePageCarouselService  {
    /**
     * 通过业务编号查询轮播图卡片
     *
     */
    public OshHomePageCarousel selectCarouselById(Long id );

    /**
     * 查询所有可见的轮播图卡片（前台展示用）
     */
    public List<OshHomePageCarousel> selectVisibleCarouselList();

    /**新增轮播图卡片
     *
     */
    public int insertCarousel(OshHomePageCarousel carousel);


    /**
     * 查询轮播图卡片列表（管理后台使用)
     */

    public List<OshHomePageCarousel> selectCarouselList(OshHomePageCarousel carousel);

    /**
     * 批量保存轮播图（整体覆盖：新增、更新、删除一次性处理）
     */
    public void saveAllCarousel(List<OshHomePageCarousel> carouselList);

    /**
     * 修改轮播图卡片
     */
    public int updateCarousel(OshHomePageCarousel carousel);

    /**
     * 批量逻辑删除轮播图卡片
     */
    public int deleteCarouselByIds(Long[] ids);

    /**
     * 逻辑删除轮播图卡片
     */
    public int deleteCarouselById(Long id);
}
