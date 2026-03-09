package com.backstage.system.service;

import com.backstage.system.domain.vo.ColumnDetailVo;
import com.backstage.system.domain.vo.ColumnListItemVo;

import java.util.List;

public interface IColumnService {

    ColumnDetailVo getColumnDetail(Long id);

    List<ColumnListItemVo> listColumnPage();
}
