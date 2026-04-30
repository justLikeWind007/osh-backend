package com.backstage.system.controller.fava;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.system.domain.fava.OshFava;
import com.backstage.system.domain.vo.search.SearchResultVo;
import com.backstage.system.service.fava.IOshFavaService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏模块 Controller
 */
@RestController
@RequestMapping("/pc")
public class OshFavaController extends BaseController {

    @Autowired
    private IOshFavaService favaService;

    /**
     * 获取我的收藏列表
     * 对应接口：http://.../pc/user_fava?page=1
     */
    @GetMapping("/user_fava")
    @Anonymous
    public AjaxResult list(@RequestParam(value = "page", defaultValue = "1") Integer page) {
//        Long userId = SecurityUtils.getUserId();
        Long userId = 1L;
        PageHelper.startPage(page, 10);
        List<SearchResultVo> list = favaService.selectFavaList(userId);
        return AjaxResult.success(getDataTable(list));
    }

    /**
     * 收藏课程或专栏
     */
    @PostMapping("/user_fava/add")
    @Anonymous
    public AjaxResult add(@RequestBody OshFava fava) {
//        fava.setUserId(SecurityUtils.getUserId());
        fava.setUserId(1L);
        return toAjax(favaService.insertFava(fava));
    }

    /**
     * 取消收藏
     */
    @PostMapping("/user_fava/remove")
    @Anonymous
    public AjaxResult remove(@RequestBody OshFava fava) {
//        fava.setUserId(SecurityUtils.getUserId());
        fava.setUserId(1L);
        return toAjax(favaService.deleteFava(fava));
    }
}