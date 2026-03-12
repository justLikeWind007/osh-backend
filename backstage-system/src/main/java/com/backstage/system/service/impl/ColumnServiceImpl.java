package com.backstage.system.service.impl;

import com.backstage.system.domain.vo.ColumnDetailVo;
import com.backstage.system.domain.vo.ColumnListItemVo;
import com.backstage.system.mapper.column.ColumnMapper;
import com.backstage.system.service.IColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Hope
 * @createTime: 2026年03月04日 22:04:37
 * @version:
 * @Description:
 */
@Service
public class ColumnServiceImpl implements IColumnService {

    @Autowired
    private ColumnMapper columnMapper;

    @Override
    public ColumnDetailVo getColumnDetail(Long id) {
        return columnMapper.getColumnDetail(id);
    }

    @Override
    public List<ColumnListItemVo> listColumnPage() {
        return columnMapper.listColumnPage();
    }
}
