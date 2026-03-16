package com.backstage.system.controller.search;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.dto.SearchQueryDto;
import com.backstage.system.service.search.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/pc/search")
public class SearchController {

    @Autowired
    private ISearchService searchService;

    @GetMapping("/index")
    @Anonymous
    public R search(@Valid SearchQueryDto queryDto) {
        // 1. 简单的参数校验
        if (StringUtils.isEmpty(queryDto.getKeyword())) {
            return R.ok(new ArrayList<>());
        }
        
        // 2. 调用 Service 层处理业务
        return searchService.searchContent(queryDto);
    }
}