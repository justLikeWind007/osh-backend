package com.backstage.system.service.tool;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.request.tool.ToolSearchRequest;

public interface IOshToolEsService {

    PageResponse<OshTool> searchTools(ToolSearchRequest request, Long userId);

    int syncAllToolsToEs();

    ToolIndexMessage buildIndexMessage(Long toolId, String eventType);
}
