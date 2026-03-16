package com.backstage.system.service.search;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.dto.SearchQueryDto;

import javax.validation.Valid;

public interface ISearchService {

    R searchContent(@Valid SearchQueryDto queryDto);
}
