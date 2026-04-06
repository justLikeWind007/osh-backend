package com.backstage.system.mapper.book;

import com.backstage.system.controller.book.BookListReqVO;
import com.backstage.system.domain.book.BookDO;
import com.backstage.system.domain.vo.book.BookListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 电子书 数据层
 *
 * @author backstage
 */
@Mapper
public interface BookMapper extends BaseMapper<BookDO> {
    /**
     * 查询电子书列表
     *
     * @param bookDO 电子书
     * @return 电子书集合
     */
    List<BookListVO> getBookPageList(Page<BookDO> pageParam, @Param("reqVO") BookListReqVO reqVO);
}
