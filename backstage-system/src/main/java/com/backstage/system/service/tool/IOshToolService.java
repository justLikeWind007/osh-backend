package com.backstage.system.service.tool;

import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolTag;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.request.tool.ToolRecommendRequest;
import com.backstage.system.request.tool.ToolSaveRequest;
import com.backstage.system.request.tool.ToolSearchRequest;

import java.util.List;

public interface IOshToolService {

    List<OshTool> pageQuerySearchTool(Long userId, ToolSearchRequest request);

    List<OshTool> listRecommendTools(Long userId, ToolRecommendRequest request);

    List<OshToolTag> listAvailableTags();

    Long createTool(ToolSaveRequest request, OshUser operator);

    Long updateTool(ToolSaveRequest request, OshUser operator);

    void deleteToolsByIds(List<Long> ids, OshUser operator);

    OshTool getToolDetail(Long toolId, Long userId);

    Integer consumeToolUsage(Long userId, String operator, Long toolId);

    Integer voteTool(Long userId, String operator, Long toolId, Integer type);
}
