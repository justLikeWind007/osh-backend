package com.backstage.system.controller.homepage;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.homepage.vo.HotSeckillVO;
import com.backstage.system.service.homepage.IOshHomePageSeckillService;
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
 * 首页热门秒杀 Controller
 *
 * @author jayTatum
 */
@Api(tags = "首页-热门秒杀")
@RestController
@RequestMapping("/pc/homepage/seckill")
public class OshHomePageSeckillController extends BaseController {

    @Autowired
    private IOshHomePageSeckillService homePageSeckillService;

    @ApiOperation("首页热门秒杀")
    @GetMapping("/hot")
    @Anonymous
    public R<List<HotSeckillVO>> hotSeckill(
            @ApiParam(value = "返回数量", defaultValue = "5")
            @RequestParam(defaultValue = "5") Integer limit) {
        return R.ok(homePageSeckillService.getHotSeckill(limit));
    }
}
