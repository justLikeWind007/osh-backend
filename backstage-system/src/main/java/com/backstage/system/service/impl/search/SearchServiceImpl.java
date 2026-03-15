package com.backstage.system.service.impl.search;

import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.dto.SearchQueryDto;
import com.backstage.system.domain.vo.search.SearchResultVo;
import com.backstage.system.mapper.search.SearchMapper;
import com.backstage.system.service.search.ISearchService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements ISearchService {

    @Autowired
    private SearchMapper searchMapper;

    @Override
    public R searchContent(SearchQueryDto dto) {
        // 使用 PageHelper 开启分页（根据你拉取的 release 代码看，项目应该集成了）
        PageHelper.startPage(dto.getPage(), 10);
        
        List<SearchResultVo> list;
        if ("column".equals(dto.getType())) {
            list = searchMapper.searchColumns(dto.getKeyword());
        } else {
            list = searchMapper.searchCourses(dto.getKeyword());
        }
        
        return R.ok(list);
    }
}