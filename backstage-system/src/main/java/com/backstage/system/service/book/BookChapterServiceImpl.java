package com.backstage.system.service.book;

import com.backstage.system.domain.BookChapter;
import com.backstage.system.mapper.book.BookChapterMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BookChapterServiceImpl extends ServiceImpl<BookChapterMapper, BookChapter> implements BookChapterService{
}
