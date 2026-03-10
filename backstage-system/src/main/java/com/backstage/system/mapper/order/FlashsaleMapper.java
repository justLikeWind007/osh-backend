package com.backstage.system.mapper.order;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface FlashsaleMapper {

    @Select("SELECT * FROM osh_course WHERE id = #{id}")
    BigDecimal selectPriceById(@Param("id") Long id);

}
