package com.backstage.system.mapper.order;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 课程 Mapper 接口
 */
@Mapper
public interface CourseMapper {
    
    /**
     * 根据 ID 查询课程价格
     * @param id 课程 ID
     * @return 课程价格
     */
    @Select("SELECT price FROM osh_course WHERE id = #{id}")
    BigDecimal selectPriceById(@Param("id") Long id);

}
