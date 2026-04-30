package com.backstage.system.mapper.column;

import com.backstage.system.domain.vo.ColumnDetailVo;
import com.backstage.system.domain.vo.ColumnListItemVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ColumnMapper {

    ColumnDetailVo getColumnDetail(Long id);

    List<ColumnListItemVo> listColumnPage();
}
