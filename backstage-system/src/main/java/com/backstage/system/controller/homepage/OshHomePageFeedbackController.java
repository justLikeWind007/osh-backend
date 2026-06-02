package com.backstage.system.controller.homepage;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.homepage.vo.HotFeedbackVO;
import com.backstage.system.service.homepage.IOshHomePageFeedbackService;
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
 * 首页热门用户反馈 Controller
 *
 * @author jayTatum
 */
@Api(tags = "首页-热门用户反馈")
@RestController
@RequestMapping("/pc/homepage/feedback")
public class OshHomePageFeedbackController extends BaseController {

    @Autowired
    private IOshHomePageFeedbackService homePageFeedbackService;

    @ApiOperation("首页热门用户反馈")
    @GetMapping("/hot")
    @Anonymous
    public R<List<HotFeedbackVO>> hotFeedback(
            @ApiParam(value = "返回数量", defaultValue = "5")
            @RequestParam(defaultValue = "5") Integer limit) {
        return R.ok(homePageFeedbackService.getHotFeedback(limit));
    }
}
