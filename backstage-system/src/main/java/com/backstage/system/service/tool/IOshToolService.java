package com.backstage.system.service.tool;

import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolTag;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.request.tool.ToolSaveRequest;
import com.backstage.system.request.tool.ToolSearchRequest;

import java.util.List;
import java.util.Map;

public interface IOshToolService {

    List<OshTool> pageQuerySearchTool(Long userId, ToolSearchRequest request);

    List<OshToolTag> listAvailableTags();

    Long createTool(ToolSaveRequest request, OshUser operator);

    Long updateTool(ToolSaveRequest request, OshUser operator);

    void deleteToolsByIds(List<Long> ids, OshUser operator);

    OshTool getToolDetail(Long toolId, Long userId);

    String getToolLogoUrl(String logoPath, int minute);

    Map<String, String> batchGetToolLogoUrlsByPaths(List<String> logoPaths, int minute);

    void fillToolLogoUrls(List<OshTool> tools, int minute);
}
