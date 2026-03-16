package com.backstage.web.controller.pc;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.Book;
import com.backstage.system.service.IBookService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的电子书Controller
 *
 * @author backstage
 */
@RestController
@RequestMapping("/pc")
public class MyBookController extends BaseBookController
{
    @Autowired
    private IBookService bookService;

    /**
     * 我购买的电子书列表
     */
    @GetMapping("/mybook")
    @Anonymous
    public R<Map<String, Object>> mybook(@RequestParam(defaultValue = "1") Integer page)
    {
        Long userId = getCurrentUserId();
        if (StringUtils.isNull(userId))
        {
            return R.fail("请先登录");
        }

        // 使用 MyBatis Plus 分页
        Page<Book> pageParam = new Page<>(page, 10);
        Page<Book> pageResult = bookService.selectUserBookListPage(userId, pageParam);

        Map<String, Object> data = new HashMap<>();
        data.put("count", pageResult.getTotal());
        data.put("rows", pageResult.getRecords());

        return R.ok(data);
    }
}
