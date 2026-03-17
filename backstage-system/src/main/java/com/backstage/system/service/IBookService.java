package com.backstage.system.service;

import com.backstage.system.domain.Book;
import com.backstage.system.domain.vo.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 电子书 服务层
 *
 * @author backstage
 */
public interface IBookService extends IService<Book>
{
    /**
     * 查询电子书列表
     *
     * @param book 电子书
     * @return 电子书集合
     */
    List<BookListVO> selectBookList(Book book);

    /**
     * 查询电子书详情
     *
     * @param id 电子书ID
     * @param userId 用户ID（可选）
     * @return 电子书详情
     */
    BookDetailVO selectBookDetail(Long id, Long userId);

    /**
     * 查询电子书章节内容
     *
     * @param bookId 电子书ID
     * @param id 章节ID
     * @param userId 用户ID（可选）
     * @return 章节内容
     */
    BookChapterContentVO selectBookChapterContent(Long bookId, Long id, Long userId);

    /**
     * 查询电子书章节菜单
     *
     * @param id 电子书ID
     * @param userId 用户ID（可选）
     * @return 章节菜单
     */
    BookMenuVO selectBookMenu(Long id, Long userId);

    /**
     * 查询用户购买的电子书列表
     *
     * @param userId 用户ID
     * @return 电子书集合
     */
    List<Book> selectUserBookList(Long userId);

    /**
     * 分页查询用户购买的电子书列表
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 电子书分页集合
     */
    Page<Book> selectUserBookListPage(Long userId, Page<Book> page);
}
