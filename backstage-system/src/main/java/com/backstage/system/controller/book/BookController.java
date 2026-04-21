package com.backstage.system.controller.book;

import com.backstage.common.annotation.OshUserEvent;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.book.*;
import com.backstage.system.service.book.IBookService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 电子书Controller
 *
 * @author backstage
 */
@ApiOperation(value = "电子书接口")
@RestController
@RequestMapping("/pc/book")
public class BookController {

    @Resource
    private IBookService bookService;

    /**
     * 电子书列表
     */
    @ApiOperation(value = "电子书列表")
    @OshUserEvent(module = "电子书模块", actionType = "查询", description = "查询电子书列表")
    @PreAuthorize("hasAuthority('book:list')")
    @PostMapping("/page")
    public R<Page<BookListVO>> list(@RequestBody BookListReqVO reqVO) {
        Page<BookListVO> pageResult = bookService.getBookPageList(reqVO);
        return R.ok(pageResult);
    }

    /**
     * 查看电子书详情
     */
    @ApiOperation(value = "电子书详情")
    @OshUserEvent(module = "电子书模块", actionType = "查询", description = "查询电子书详情")
    @PreAuthorize("hasAuthority('book:detail')")
    @GetMapping("/getById")
    public R<BookDetailVO> getById(@RequestParam Long id, @RequestParam(required = false, defaultValue = "false") Boolean forEdit) {
        BookDetailVO detail = bookService.selectBookDetail(id, forEdit);
        return R.ok(detail);
    }

    /**
     * 查看电子书章节内容
     */
    @ApiOperation(value = "章节内容详情")
    @OshUserEvent(module = "电子书模块", actionType = "查询", description = "查询章节内容")
    @PreAuthorize("hasAuthority('book:chapter:detail')")
    @GetMapping("/detail")
    public R<BookChapterContentVO> detail(@RequestParam Long book_id, @RequestParam Long id) {
        BookChapterContentVO content = bookService.selectBookChapterContent(book_id, id);
        return R.ok(content);
    }

    /**
     * 查看电子书章节菜单
     */
    @ApiOperation(value = "章节菜单")
    @OshUserEvent(module = "电子书模块", actionType = "查询", description = "查询章节菜单")
    @PreAuthorize("hasAuthority('book:chapter:menus')")
    @GetMapping("/menus")
    public R<BookMenuVO> menus(@RequestParam Long id) {
        BookMenuVO menu = bookService.selectBookMenu(id);
        return R.ok(menu);
    }


    /**
     * 新增电子书
     */
    @ApiOperation(value = "新增电子书")
    @OshUserEvent(module = "电子书模块", actionType = "新增", description = "创建电子书")
    @PreAuthorize("hasAuthority('book:create')")
    @PostMapping("/create")
    public R<String> create(@RequestBody BookSaveReqVO reqVO) {
        bookService.createBook(reqVO);
        return R.ok( "创建成功");
    }

    /**
     * 修改电子书
     */
    @ApiOperation(value = "修改电子书")
    @OshUserEvent(module = "电子书模块", actionType = "修改", description = "更新电子书")
    @PreAuthorize("hasAuthority('book:update')")
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
    @ApiOperation(value = "删除电子书")
    @OshUserEvent(module = "电子书模块", actionType = "删除", description = "删除电子书")
    @PreAuthorize("hasAuthority('book:delete')")
    @DeleteMapping("/delete")
    public R<String> delete(@RequestParam("id") Long id)
    {
        bookService.deleteBook(id);
        return R.ok("删除成功");
    }

    /**
     * 查询所有电子书标签列表
     */
    @ApiOperation(value = "标签列表")
    @OshUserEvent(module = "电子书模块", actionType = "查询", description = "查询标签列表")
    @PreAuthorize("hasAuthority('book:tag:list')")
    @GetMapping("/getTagList")
    public R<List<String>> getTagList() {
        return R.ok(bookService.getTagList());
    }

    /**
     * 新增电子书章节
     */
    @ApiOperation(value = "新增章节")
    @OshUserEvent(module = "电子书模块", actionType = "新增", description = "创建章节")
    @PreAuthorize("hasAuthority('book:chapter:create')")
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
    @ApiOperation(value = "修改章节")
    @OshUserEvent(module = "电子书模块", actionType = "修改", description = "更新章节")
    @PreAuthorize("hasAuthority('book:chapter:update')")
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

    /**
     * 筛选电子书列表
     */
    @ApiOperation(value = "筛选电子书")
    @OshUserEvent(module = "电子书模块", actionType = "查询", description = "筛选电子书")
    @PreAuthorize("hasAuthority('book:filter')")
    @GetMapping("/getFilterBookList")
    public R<Page<BookListVO>> getFilterBookList(@RequestParam String filter) {
        Page<BookListVO> bookList = bookService.getFilterBookList(filter);
        return R.ok(bookList);
    }



}
