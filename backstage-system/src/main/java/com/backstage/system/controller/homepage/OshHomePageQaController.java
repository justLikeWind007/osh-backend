package com.backstage.system.controller.homepage;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.homepage.vo.HotQaVO;
import com.backstage.system.service.homepage.IOshHomePageQaService;
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
 * 首页热门答疑 Controller
 *
 * @author jayTatum
 */
@Api(tags = "首页-热门答疑")
@RestController
@RequestMapping("/pc/homepage/qa")
public class OshHomePageQaController extends BaseController {

    @Autowired
    private IOshHomePageQaService homePageQaService;

    @ApiOperation("首页热门答疑")
    @GetMapping("/hot")
    @Anonymous
    public R<List<HotQaVO>> hotQa(
            @ApiParam(value = "返回数量", defaultValue = "3")
            @RequestParam(defaultValue = "3") Integer limit) {
        return R.ok(homePageQaService.getHotQa(limit));
    }
}
