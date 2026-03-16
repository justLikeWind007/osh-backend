package com.backstage.system.mapper.search;

import com.backstage.system.domain.vo.search.SearchResultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SearchMapper {
    List<SearchResultVo> searchCourses(@Param("keyword") String keyword);
    List<SearchResultVo> searchColumns(@Param("keyword") String keyword);
}