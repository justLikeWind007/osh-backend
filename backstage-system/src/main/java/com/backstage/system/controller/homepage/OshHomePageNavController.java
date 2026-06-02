package com.backstage.system.controller.homepage;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.homepage.vo.NavModuleVO;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 首页导航模块 Controller
 * <p>
 * 提供各模块"查看全部"按钮的前端跳转路径，
 * 路径统一由 IOshHomePageModulePathService 管理。
 *
 * @author jayTatum
 */
@Api(tags = "首页-导航模块路径")
@RestController
@RequestMapping("/pc/homepage/nav")
public class OshHomePageNavController extends BaseController {

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    /**
     * 获取各模块列表页路径（无需登录）
     */
    @ApiOperation("获取首页各模块列表页路径")
    @GetMapping("/modules")
    @Anonymous
    public R<List<NavModuleVO>> getNavModules() {
        List<NavModuleVO> modules = Arrays.asList(
                new NavModuleVO("course",      "课程",     modulePathService.getListPath("course")),
                new NavModuleVO("book",        "电子书",   modulePathService.getListPath("book")),
                new NavModuleVO("exam",        "试卷",     modulePathService.getListPath("exam")),
                new NavModuleVO("feedback",    "用户反馈", modulePathService.getListPath("feedback")),
                new NavModuleVO("group",       "拼团",     modulePathService.getListPath("group")),
                new NavModuleVO("info_gap",    "信息差",   modulePathService.getListPath("info_gap")),
                new NavModuleVO("openproject", "开源项目", modulePathService.getListPath("openproject")),
                new NavModuleVO("qa",          "问答",     modulePathService.getListPath("qa")),
                new NavModuleVO("seckill",     "秒杀",     modulePathService.getListPath("seckill")),
                new NavModuleVO("tool",        "工具",     modulePathService.getListPath("tool")),
                new NavModuleVO("usefull",     "实用网站", modulePathService.getListPath("usefull"))
        );
        return R.ok(modules);
    }
}
