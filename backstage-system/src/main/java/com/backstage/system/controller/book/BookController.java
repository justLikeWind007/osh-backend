package com.backstage.system.controller.book;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.system.domain.book.BookDO;
import com.backstage.system.domain.vo.*;
import com.backstage.system.service.IBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 电子书Controller
 *
 * @author backstage
 */
@RestController
@RequestMapping("/pc/book")
public class BookController {
    @Autowired
    private IBookService bookService;

    /**
     * 电子书列表
     */
    @Anonymous
    @GetMapping("/list")
    public R<Page<BookDO>> list(@RequestParam(defaultValue = "1") Integer page) {
        Page<BookDO> pageParam = new Page<>(page, 10);
        LambdaQueryWrapper<BookDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookDO::getStatus, "0")
                .orderByDesc(BookDO::getCreateTime);

        Page<BookDO> pageResult = bookService.page(pageParam, wrapper);
        return R.ok(pageResult);
    }

    /**
     * 查看电子书详情
     */
    @Anonymous
    @GetMapping("/getById")
    public R<BookDetailVO> getById(@RequestParam Long id) {
        Long userId = getCurrentUserId();
        BookDetailVO detail = bookService.selectBookDetail(id, userId);
        return R.ok(detail);
    }

    /**
     * 查看电子书章节内容
     */
    @Anonymous
    @GetMapping("/detail")
    public R<BookChapterContentVO> detail(@RequestParam Long book_id, @RequestParam Long id) {
        Long userId = getCurrentUserId();
        BookChapterContentVO content = bookService.selectBookChapterContent(book_id, id, userId);
        return R.ok(content);
    }

    /**
     * 查看电子书章节菜单
     */
    @Anonymous
    @GetMapping("/menus")
    public R<BookMenuVO> menus(@RequestParam Long id) {
        Long userId = getCurrentUserId();
        BookMenuVO menu = bookService.selectBookMenu(id, userId);
        return R.ok(menu);
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        return SecurityUtils.getUserId();
    }

    /**
     * 新增电子书
     */
    @Anonymous
    @PostMapping("/create")
    public R<String> create(@RequestBody BookSaveReqVO reqVO) {
        bookService.createBook(reqVO);
        return R.ok( "创建成功");
    }

    /**
     * 修改电子书
     */
    @Anonymous
    @PostMapping("/update")
    public R<String> update(@Valid @RequestBody BookSaveReqVO reqVO) {
        if (reqVO.getId() == null)
        {
            return R.fail("电子书ID不能为空");
        }
        bookService.updateBook(reqVO);
        return R.ok( "修改成功");
    }

    /**
     * 删除电子书
     */
    @Anonymous
    @DeleteMapping("/delete")
    public R<String> delete(@RequestParam("id") Long id)
    {
        bookService.deleteBook(id);
        return R.ok("删除成功");
    }

}
