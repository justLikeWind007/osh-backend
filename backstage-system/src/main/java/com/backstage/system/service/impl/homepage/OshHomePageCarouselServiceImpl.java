package com.backstage.system.service.impl.homepage;

import com.backstage.common.utils.SecurityUtils;
import com.backstage.system.domain.homepage.OshHomePageCarousel;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.homepage.OshHomePageCarouselMapper;
import com.backstage.system.service.homepage.IOshHomePageCarouselService;
import com.backstage.system.utils.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OshHomePageCarouselServiceImpl implements IOshHomePageCarouselService {

    @Autowired
    private OshHomePageCarouselMapper carouselMapper;

    /**
     * 获取当前操作用户名（模仿课程模块，从 ThreadLocal 获取前台用户信息）
     */
    private String getCurrentUsername() {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        return currentUser != null ? currentUser.getUsername() : "system";
    }

    @Override
    public OshHomePageCarousel selectCarouselById(Long id)
    {
        return carouselMapper.selectCarouselById(id);
    }

    @Override
    public List<OshHomePageCarousel> selectVisibleCarouselList() {
        return carouselMapper.selectVisibleCarouselList();
    }

    @Override
    public int insertCarousel(OshHomePageCarousel carousel) {
        carousel.setCreateBy(SecurityUtils.getUsername());
        return carouselMapper.insertCarousel(carousel);

    }

    @Override
    public List<OshHomePageCarousel> selectCarouselList(OshHomePageCarousel carousel) {
        return carouselMapper.selectCarouselList(carousel);
    }

    @Override
    public void saveAllCarousel(List<OshHomePageCarousel> carouselList) {
        String username = getCurrentUsername();

        List<Long> existingIds = carouselMapper.selectAllCarouselIds();
        //前端传来的id集合
        Set<Long> inconmingIds = carouselList.stream()
                .filter(c->c.getId()!=null)
                .map(OshHomePageCarousel::getId)
                .collect(Collectors.toSet());
        for(Long existingId:existingIds){
            if(!inconmingIds.contains(existingId)){
                carouselMapper.deleteCarouselById(existingId);

            }

        }
        // 遍历前端列表，按顺序设置 sort
        for (int i = 0; i < carouselList.size(); i++) {
            OshHomePageCarousel item = carouselList.get(i);
            item.setSort(i + 1);

            if (item.getId() != null) {
                // 有 id → 更新
                item.setUpdateBy(username);
                carouselMapper.updateCarousel(item);
            } else {
                // 没有 id → 新增
                item.setCreateBy(username);
                carouselMapper.insertCarousel(item);
            }
        }


    }

    @Override
    public int updateCarousel(OshHomePageCarousel carousel) {
        carousel.setUpdateBy(SecurityUtils.getUsername());
        return carouselMapper.updateCarousel(carousel);
    }

    @Override
    public int deleteCarouselByIds(Long[] ids) {
        return  carouselMapper.deleteCarouselByIds(ids);
    }

    @Override
    public int deleteCarouselById(Long id) {
        return carouselMapper.deleteCarouselById(id);

    }
}
