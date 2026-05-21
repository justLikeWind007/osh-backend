package com.backstage.system.controller.homepage;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.homepage.vo.HotBookVO;
import com.backstage.system.service.homepage.IOshHomePageBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 首页热门电子书 Controller
 *
 * @author jayTatum
 */
@Api(tags = "首页-热门电子书")
@RestController
@RequestMapping("/pc/homepage/book")
public class OshHomePageBookController extends BaseController {

    @Autowired
    private IOshHomePageBookService homePageBookService;

    /**
     * 首页热门电子书（无需登录）
     * 基于热度公式排序，返回 Top N 电子书及其标签
     */
    @ApiOperation("首页热门电子书")
    @GetMapping("/hot")
    @Anonymous
    public R<List<HotBookVO>> hotBooks(
            @ApiParam(value = "返回数量", defaultValue = "5")
            @RequestParam(defaultValue = "5") Integer limit) {
        return R.ok(homePageBookService.getHotBooks(limit));
    }
}
