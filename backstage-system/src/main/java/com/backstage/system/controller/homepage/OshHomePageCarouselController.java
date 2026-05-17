package com.backstage.system.controller.homepage;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.enums.BusinessType;
import com.backstage.common.utils.poi.ExcelUtil;
import com.backstage.system.domain.homepage.OshHomePageCarousel;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.service.homepage.IOshHomePageCarouselService;
import com.backstage.system.utils.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 首页轮播图卡片Controller
 * @author jayTatum
 *
 */

@RestController
@RequestMapping("/pc/homepage/carousel")
public class OshHomePageCarouselController  extends BaseController {
    @Autowired
    private IOshHomePageCarouselService homePageCarouselService;

    /**
     * 前台查询可见轮播图
     */
    @Anonymous
    @GetMapping("/visible")
    public R<List<OshHomePageCarousel>> visibleCarousel(){

        List<OshHomePageCarousel> list = homePageCarouselService.selectVisibleCarouselList();
        return R.ok(list);

    }

    /**
     * 管理员后台查询轮播图列表(不分页，编辑弹窗用)
     *
     */

    @PreAuthorize("hasAuthority('homepage:carousel:list')")
    @GetMapping("/list")
    public R<List<OshHomePageCarousel>> list(OshHomePageCarousel carousel){

        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) return R.fail("请先登录");
        List<OshHomePageCarousel> list = homePageCarouselService.selectCarouselList(carousel);
        return R.ok(list);
    }

//    /**
//     * 导出轮播图列表
//     */
//    @PreAuthorize("@ss.hasPermi('homepage:carousel:export')")
//    @Log(title = "首页轮播图", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, OshHomePageCarousel carousel)
//    {
//        List<OshHomePageCarousel> list = homePageCarouselService.selectCarouselList(carousel);
//        ExcelUtil<OshHomePageCarousel> util = new ExcelUtil<>(OshHomePageCarousel.class);
//        util.exportExcel(response, list, "首页轮播图数据");
//    }

    /**
     * 获取轮播图卡片详细信息
     */
    @PreAuthorize("hasAuthority('homepage:carousel:query')")
    @GetMapping(value = "/{id}")
    public R<OshHomePageCarousel> getInfo(@PathVariable("id") Long id)
    {
        OshHomePageCarousel data = homePageCarouselService.selectCarouselById(id);
        return data != null ? R.ok(data) : R.fail("查询不到该轮播图卡片");
    }

    /**
     * 新增轮播图卡片
     *
     */
    @PreAuthorize("hasAuthority('homepage:carousel:add')")
    @Log(title = "首页轮播图", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Integer> add(@RequestBody OshHomePageCarousel carousel){

        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) return R.fail("请先登录");
        return R.ok(homePageCarouselService.insertCarousel(carousel));
    }

    /**
     * 批量保存轮播图（整体覆盖：新增、更新、删除一次性处理）
     * 前端"保存全部"按钮调用此接口
     */
    @PreAuthorize("hasAuthority('homepage:carousel:edit')")
    @Log(title = "首页轮播图", businessType = BusinessType.UPDATE)
    @PostMapping("/saveAll")
    public R<String> saveAll(@RequestBody List<OshHomePageCarousel> carouselList)
    {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) return R.fail("请先登录");
        homePageCarouselService.saveAllCarousel(carouselList);
        return R.ok("保存成功");
    }

    /**
     * 修改轮播图卡片
     */
    @PreAuthorize("hasAuthority('homepage:carousel:edit')")
    @Log(title = "首页轮播图", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Integer> edit(@RequestBody OshHomePageCarousel carousel)
    {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) return R.fail("请先登录");
        return R.ok(homePageCarouselService.updateCarousel(carousel));
    }

    /**
     * 删除轮播图卡片
     */
    @PreAuthorize("hasAuthority('homepage:carousel:remove')")
    @Log(title = "首页轮播图", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Integer> remove(@PathVariable Long[] ids)
    {
        OshUser currentUser = UserContextUtil.getCurrentUser();
        if (currentUser == null) return R.fail("请先登录");
        return R.ok(homePageCarouselService.deleteCarouselByIds(ids));
    }











}
