package com.backstage.system.controller.book;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.book.*;
import com.backstage.system.service.IBookService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 电子书Controller
 *
 * @author backstage
 */
@RestController
@RequestMapping("/pc/book")
public class BookController {

    @Resource
    private IBookService bookService;

    /**
     * 电子书列表
     */
    @Anonymous
    @PostMapping("/page")
    public R<Page<BookListVO>> list(@RequestBody BookListReqVO reqVO) {
        Page<BookListVO> pageResult = bookService.getBookPageList( reqVO);
        return R.ok(pageResult);
    }

    /**
     * 查看电子书详情
     */
    @Anonymous
    @GetMapping("/getById")
    public R<BookDetailVO> getById(@RequestParam Long id) {
        BookDetailVO detail = bookService.selectBookDetail(id);
        return R.ok(detail);
    }

    /**
     * 查看电子书章节内容
     */
    @Anonymous
    @GetMapping("/detail")
    public R<BookChapterContentVO> detail(@RequestParam Long book_id, @RequestParam Long id) {
        BookChapterContentVO content = bookService.selectBookChapterContent(book_id, id);
        return R.ok(content);
    }

    /**
     * 查看电子书章节菜单
     */
    @Anonymous
    @GetMapping("/menus")
    public R<BookMenuVO> menus(@RequestParam Long id) {
        BookMenuVO menu = bookService.selectBookMenu(id);
        return R.ok(menu);
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

    /**
     * 查询所有电子书标签列表
     */
    @Anonymous
    @GetMapping("/getTagList")
    public R<List<String>> getTagList() {
        return R.ok(bookService.getTagList());
    }

    /**
     * 新增电子书章节
     */
    @Anonymous
    @ApiOperation(value = "新增电子书章节")
    @PostMapping("/chapter/create")
    public R<String> createBookChapter(@RequestBody BookChapterSaveUpdateVO reqVO) {
        if (reqVO.getBookId() == null) {
            return R.fail("电子书ID不能为空");
        }
        bookService.createBookChapter(reqVO);
        return R.ok("创建成功");
    }

    /**
     * 修改电子书章节
     */
    @Anonymous
    @ApiOperation(value = "修改电子书章节")
    @PostMapping("/chapter/update")
    public R<String> updateBookChapter(@RequestBody BookChapterSaveUpdateVO reqVO) {
        if (reqVO.getId() == null) {
            return R.fail("章节ID不能为空");
        }
        if (reqVO.getBookId() == null) {
            return R.fail("电子书ID不能为空");
        }
        bookService.updateBookChapter(reqVO);
        return R.ok("修改成功");
    }
}
