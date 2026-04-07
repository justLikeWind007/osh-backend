package com.backstage.system.mapper.order;

import java.math.BigDecimal;

import com.backstage.system.domain.book.BookDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 电子书 Mapper 接口
 */
@Mapper
public interface OshBookMapper {
    
    /**
     * 根据 ID 查询电子书价格
     * @param id 电子书 ID
     * @return 电子书价格
     */
    @Select("SELECT price FROM osh_book WHERE id = #{id}")
    BigDecimal selectPriceById(@Param("id") Long id);

    @Select("SELECT * FROM osh_book WHERE id = #{id}")
    BookDO selectById(@Param("id") Long id);
}
