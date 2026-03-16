package com.backstage.system.mapper;

import com.backstage.system.domain.Book;
import com.backstage.system.domain.vo.BookListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 电子书 数据层
 *
 * @author backstage
 */
@Mapper
public interface BookMapper extends BaseMapper<Book>
{
    /**
     * 查询电子书列表
     *
     * @param book 电子书
     * @return 电子书集合
     */
    List<BookListVO> selectBookList(Book book);
}
