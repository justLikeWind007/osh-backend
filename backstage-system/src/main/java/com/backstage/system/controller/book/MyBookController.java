package com.backstage.system.controller.book;

import com.backstage.common.annotation.OshUserActionLog;
import com.backstage.common.core.domain.R;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.book.BookDO;
import com.backstage.system.service.book.IBookService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的电子书Controller
 *
 * @author backstage
 */
@RestController
@RequestMapping("/pc")
public class MyBookController {
    @Resource
    private IBookService bookService;

    /**
     * 我购买的电子书列表
     */
    @ApiOperation(value = "我的电子书列表")
    @OshUserActionLog(module = "电子书模块", actionType = "查询", description = "查询我的电子书")
    @PreAuthorize("hasAuthority('book:my:list')")
    @GetMapping("/mybook")
    public R<Map<String, Object>> myBook(@RequestParam(defaultValue = "1") Integer page)
    {
        Long userId = SecurityUtils.getUserId();
        if (StringUtils.isNull(userId))
        {
            return R.fail("请先登录");
        }

        // 使用 MyBatis Plus 分页
        Page<BookDO> pageParam = new Page<>(page, 10);
        Page<BookDO> pageResult = bookService.selectUserBookListPage(userId, pageParam);

        Map<String, Object> data = new HashMap<>();
        data.put("count", pageResult.getTotal());
        data.put("rows", pageResult.getRecords());

        return R.ok(data);
    }
}
