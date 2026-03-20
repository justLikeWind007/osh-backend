package com.backstage.system.controller.bbs;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.bbs.OshBbsCategory;
import com.backstage.system.domain.vo.bbs.BbsSummaryVo;
import com.backstage.system.service.bbs.IOshBbsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pc")
public class OshBbsCategoryController extends BaseController {

    @Autowired
    private IOshBbsCategoryService categoryService;

    /**
     * 社区概览列表
     */
    @GetMapping("/bbs")
    @Anonymous
    public R getBbsSummary(OshBbsCategory category) {
        // 分页处理
        startPage();
        BbsSummaryVo summary = categoryService.getBbsSummary(category);
        // 按照你要求的 JSON 结构手动组装
        R r = R.ok();
        r.setData( summary);
        return r;
    }
}