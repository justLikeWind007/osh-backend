package com.backstage.system.mapper;

import com.backstage.system.domain.Book;
import com.backstage.system.domain.UserBook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 用户电子书 数据层
 *
 * @author backstage
 */
public interface UserBookMapper extends BaseMapper<UserBook>
{
    /**
     * 查询用户是否购买电子书
     *
     * @param userId 用户ID
     * @param bookId 电子书ID
     * @return 用户电子书
     */
    UserBook selectUserBookByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

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
    Page<Book> selectUserBookListPage(@Param("userId") Long userId, Page<Book> page);
}
