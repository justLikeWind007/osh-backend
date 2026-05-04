package com.backstage.system.service.tool;

import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.request.tool.ToolSaveRequest;

import java.util.List;

public interface IOshToolService {

    Long createTool(ToolSaveRequest request, OshUser operator);

    Long updateTool(ToolSaveRequest request, OshUser operator);

    void deleteToolsByIds(List<Long> ids, OshUser operator);

    OshTool getToolDetail(Long toolId, Long userId);
}
