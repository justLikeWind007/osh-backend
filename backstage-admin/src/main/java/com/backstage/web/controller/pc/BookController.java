package com.backstage.web.controller.pc;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.Book;
import com.backstage.system.domain.vo.*;
import com.backstage.system.service.IBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 电子书Controller
 *
 * @author backstage
 */
@RestController
@RequestMapping("/pc/book")
public class BookController extends BaseBookController {
    @Autowired
    private IBookService bookService;

    /**
     * 电子书列表
     */
    @Anonymous
    @GetMapping("/list")
    public R<Page<Book>> list(@RequestParam(defaultValue = "1") Integer page) {
        Page<Book> pageParam = new Page<>(page, 10);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, "0")
                .orderByDesc(Book::getCreateTime);

        Page<Book> pageResult = bookService.page(pageParam, wrapper);
        return R.ok(pageResult);
    }

    /**
     * 查看电子书详情
     */
    @Anonymous
    @GetMapping("/read")
    public R<BookDetailVO> read(@RequestParam Long id) {
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
}
