package com.backstage.system.service.impl;

import com.backstage.common.exception.ServiceException;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.controller.book.BookListReqVO;
import com.backstage.system.domain.book.BookDO;
import com.backstage.system.domain.BookChapter;
import com.backstage.system.domain.UserBook;
import com.backstage.system.domain.book.BookTagDO;
import com.backstage.system.domain.vo.book.*;
import com.backstage.system.mapper.book.BookChapterMapper;
import com.backstage.system.mapper.book.BookMapper;
import com.backstage.system.mapper.UserBookMapper;
import com.backstage.system.mapper.book.BookTagDOMapper;
import com.backstage.system.service.IBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 电子书 服务层实现
 *
 * @author backstage
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, BookDO> implements IBookService
{
    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookChapterMapper bookChapterMapper;

    @Autowired
    private UserBookMapper userBookMapper;

    @Resource
    private BookTagDOMapper bookTagDOMapper;

    /**
     * 查询电子书列表
     *
     * @param book 电子书
     * @return 电子书集合
     */
    /**
     * 查询电子书列表
     *
     * @param reqVO 电子书
     * @return 电子书集合
     */

    @Override
    public Page<BookListVO> getBookPageList(BookListReqVO reqVO) {
        Page<BookDO> pageParam = new Page<>(reqVO.getPageNum(), reqVO.getPageSize());
        List<BookListVO> bookListVOS = bookMapper.getBookPageList(pageParam, reqVO);

        // tagNames 逗号字符串 -> tagNameList 数组
        for (BookListVO vo : bookListVOS) {
            if (StringUtils.isNotEmpty(vo.getTagNames())) {
                vo.setTagNameList(Arrays.asList(vo.getTagNames().split(",")));
            }
        }

        Page<BookListVO> result = new Page<>(pageParam.getCurrent(), pageParam.getSize(), pageParam.getTotal());
        result.setRecords(bookListVOS);
        return result;
    }

    @Override
    public List<String> getTagList() {
        return bookTagDOMapper.getTagList();
    }

    /**
     * 查询电子书详情
     *
     * @param id 电子书ID
     * @param userId 用户ID（可选）
     * @return 电子书详情
     */
    @Override
    public BookDetailVO selectBookDetail(Long id, Long userId) {
        BookDO bookDO = getById(id);
        checkEntityNotNull(bookDO, "该记录不存在");

        BookDetailVO vo = new BookDetailVO();
        BeanUtils.copyProperties(bookDO, vo);
        vo.setDesc(bookDO.getDescription());
        vo.setTryContent(bookDO.getTryContent());
        vo.setPrice(Optional.ofNullable(bookDO.getPrice()).map(Object::toString).orElse("0"));
        vo.setTPrice(Optional.ofNullable(bookDO.getOriginalPrice()).map(Object::toString).orElse("0"));
        vo.setSubCount(Optional.ofNullable(bookDO.getSubCount()).orElse(0));

        // 查询章节列表
        List<BookChapterVO> chapters = bookChapterMapper.selectBookChapterListByBookId(id);
        vo.setBookDetails(chapters);

        // 检查用户是否购买
        vo.setIsbuy(checkUserHasBought(userId, id));
        // 获取标签列表
        vo.setTags(bookTagDOMapper.selectBookTagListByBookId(id));

        return vo;
    }

    /**
     * 查询电子书章节内容
     *
     * @param bookId 电子书ID
     * @param id 章节ID
     * @param userId 用户ID（可选）
     * @return 章节内容
     */
    @Override
    public BookChapterContentVO selectBookChapterContent(Long bookId, Long id, Long userId) {
        BookChapter chapter = bookChapterMapper.selectBookChapterByBookIdAndId(bookId, id);
        checkEntityNotNull(chapter, "该记录不存在");

        // 如果不是免费章节，检查用户是否购买
        if (chapter.getIsfree() == 0)
        {
            checkUserHasBoughtOrThrow(userId, bookId);
        }

        BookChapterContentVO vo = new BookChapterContentVO();
        BeanUtils.copyProperties(chapter, vo);

        return vo;
    }

    /**
     * 查询电子书章节菜单
     *
     * @param id 电子书ID
     * @param userId 用户ID（可选）
     * @return 章节菜单
     */
    @Override
    public BookMenuVO selectBookMenu(Long id, Long userId) {
        BookDO bookDO = getById(id);
        checkEntityNotNull(bookDO, "该记录不存在");

        BookMenuVO vo = new BookMenuVO();

        // 设置电子书基本信息
        BookSimpleVO simpleVO = new BookSimpleVO();
        BeanUtils.copyProperties(bookDO, simpleVO);
        vo.setDetail(simpleVO);

        // 查询章节列表
        List<BookChapterVO> chapters = bookChapterMapper.selectBookChapterListByBookId(id);
        vo.setMenus(chapters);

        return vo;
    }

    /**
     * 查询用户购买的电子书列表
     *
     * @param userId 用户ID
     * @return 电子书集合
     */
    @Override
    public List<BookDO> selectUserBookList(Long userId)
    {
        return userBookMapper.selectUserBookList(userId);
    }

    /**
     * 分页查询用户购买的电子书列表
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 电子书分页集合
     */
    @Override
    public Page<BookDO> selectUserBookListPage(Long userId, Page<BookDO> page) {
        return userBookMapper.selectUserBookListPage(userId, page);
    }

    /**
     * 新增电子书
     *
     * @param reqVO 电子书请求VO
     * @return 电子书响应VO
     */
    @Override
    @Transactional
    public void createBook(BookSaveReqVO reqVO) {

        BookDO bookDO = new BookDO();
        BeanUtils.copyProperties(reqVO, bookDO);
        bookDO.setStatus("0");
        // 新增电子书
        bookMapper.insert(bookDO);
        // 新增电子书标签表记录
        List<BookTagDO> bookTagDOS = packageBookTagInsertDOList(bookDO.getId(), reqVO);
        bookTagDOMapper.insert(bookTagDOS);
    }

    /**
     * 封装标签数据
     *
     * @param reqVO 电子书DO
     */
    private List<BookTagDO> packageBookTagInsertDOList(Long id, BookSaveReqVO reqVO) {
        List<BookTagDO> insertList = new ArrayList<>();
        for (int i = 0; i < reqVO.getTags().size(); i++) {
            BookTagDO bookTagInsertDO = new BookTagDO();
            bookTagInsertDO.setBookId(id);
            bookTagInsertDO.setTagName(reqVO.getTags().get(i));
            bookTagInsertDO.setSortOrder(i + 1);
            insertList.add(bookTagInsertDO);
        }
        return insertList;
    }

    /**
     * 修改电子书
     *
     * @param reqVO 电子书请求VO
     * @return 电子书响应VO
     */
    @Override
    public void updateBook(BookSaveReqVO reqVO)
    {
        BookDO book = getById(reqVO.getId());
        checkEntityNotNull(book, "电子书不存在");
        // 新增
        BookDO bookDO = new BookDO();
        BeanUtils.copyProperties(reqVO, bookDO);
        bookMapper.updateById(bookDO);

        // 更新电子书标签
        // 先删除该电子书原有的标签
        bookTagDOMapper.delete(
                new LambdaQueryWrapper<BookTagDO>()
                        .eq(BookTagDO::getBookId, reqVO.getId())
        );
        // 再删除新的标签
        List<BookTagDO> bookTagDOS = packageBookTagInsertDOList(reqVO.getId(), reqVO);
        bookTagDOMapper.insert(bookTagDOS);
    }

    /**
     * 删除电子书（逻辑删除）
     *
     * @param id 电子书ID
     */
    @Override
    public void deleteBook(Long id)
    {
        BookDO bookDO = getById(id);
        checkEntityNotNull(bookDO, "电子书不存在");
        bookMapper.deleteById(id);
    }

    /**
     * 检查实体是否为空，为空则抛出异常
     *
     * @param entity 实体对象
     * @param message 异常信息
     */
    private void checkEntityNotNull(Object entity, String message)
    {
        if (StringUtils.isNull(entity)) {
            throw new ServiceException(message);
        }
    }

    /**
     * 检查用户是否购买电子书
     *
     * @param userId 用户ID
     * @param bookId 电子书ID
     * @return 是否购买
     */
    private boolean checkUserHasBought(Long userId, Long bookId)
    {
        if (StringUtils.isNull(userId)) {
            return false;
        }
        UserBook userBook = userBookMapper.selectUserBookByUserIdAndBookId(userId, bookId);
        return StringUtils.isNotNull(userBook);
    }

    /**
     * 检查用户是否购买电子书，未购买则抛出异常
     *
     * @param userId 用户ID
     * @param bookId 电子书ID
     */
    private void checkUserHasBoughtOrThrow(Long userId, Long bookId)
    {
        if (StringUtils.isNull(userId)) {
            throw new ServiceException("请先购买该电子书");
        }
        UserBook userBook = userBookMapper.selectUserBookByUserIdAndBookId(userId, bookId);
        if (StringUtils.isNull(userBook)) {
            throw new ServiceException("请先购买该电子书");
        }
    }
}
