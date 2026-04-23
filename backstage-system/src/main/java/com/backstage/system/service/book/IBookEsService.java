package com.backstage.system.service.book;

import com.backstage.system.controller.book.BookListReqVO;
import com.backstage.system.domain.vo.book.BookListVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface IBookEsService {

    Page<BookListVO> searchBooks(BookListReqVO request);

    int syncAllBooksToEs();

    void syncBookToEs(Long bookId);

    void deleteBookFromEs(Long bookId);
}
