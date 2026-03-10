package com.backstage.system.mapper.order;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 专栏 Mapper 接口
 */
@Mapper
public interface ColumnMapper {
    
    /**
     * 根据 ID 查询专栏价格
     * @param id 专栏 ID
     * @return 专栏价格
     */
    @Select("SELECT price FROM osh_column WHERE id = #{id}")
    BigDecimal selectPriceById(@Param("id") Long id);
}
