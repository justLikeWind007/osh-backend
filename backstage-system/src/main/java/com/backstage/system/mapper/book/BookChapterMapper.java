package com.backstage.system.mapper.book;

import com.backstage.system.domain.BookChapter;
import com.backstage.system.domain.vo.BookChapterVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 电子书章节 数据层
 *
 * @author backstage
 */
public interface BookChapterMapper extends BaseMapper<BookChapter>
{
    /**
     * 根据电子书ID和章节ID查询章节
     *
     * @param bookId 电子书ID
     * @param id 章节ID
     * @return 电子书章节
     */
    BookChapter selectBookChapterByBookIdAndId(@Param("bookId") Long bookId, @Param("id") Long id);

    /**
     * 查询电子书章节列表
     *
     * @param bookId 电子书ID
     * @return 电子书章节集合
     */
    List<BookChapterVO> selectBookChapterListByBookId(Long bookId);
}
