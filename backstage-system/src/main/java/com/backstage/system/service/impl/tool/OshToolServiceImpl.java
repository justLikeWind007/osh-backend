package com.backstage.system.service.impl.tool;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolTag;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.tool.OshToolCollectionMapper;
import com.backstage.system.mapper.tool.OshToolMapper;
import com.backstage.system.mapper.tool.OshToolTagMapper;
import com.backstage.system.request.tool.ToolSaveRequest;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.tool.IOshToolService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class OshToolServiceImpl implements IOshToolService {

    private static final int LOGO_URL_EXPIRE_MINUTES = 1440;
    private static final int ACCESS_TYPE_INTERNAL = 1;
    private static final int ACCESS_TYPE_IFRAME = 2;
    private static final String DEFAULT_RESOURCE_TYPE = "FREE";

    @Autowired
    private OshToolMapper oshToolMapper;

    @Autowired
    private OshToolTagMapper oshToolTagMapper;

    @Autowired
    private OshToolCollectionMapper oshToolCollectionMapper;

    @Autowired
    private OssService ossService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTool(ToolSaveRequest request, OshUser operator) {
        if (StringUtils.isBlank(request.getToolName())) {
            throw new IllegalArgumentException("工具名称不能为空");
        }
        OshTool tool = buildTool(request, operator.getUsername());
        if (oshToolMapper.insertTool(tool) <= 0) {
            throw new ServiceException("新增工具失败");
        }
        syncToolTags(tool.getId(), request.getTags(), operator.getUsername());
        return tool.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateTool(ToolSaveRequest request, OshUser operator) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("工具ID不能为空");
        }
        validateToolExists(request.getId());
        OshTool tool = buildTool(request, operator.getUsername());
        tool.setId(request.getId());
        if (oshToolMapper.updateTool(tool) <= 0) {
            throw new ServiceException("修改工具失败");
        }
        if (request.getTags() != null) {
            syncToolTags(tool.getId(), request.getTags(), operator.getUsername());
        }
        return tool.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteToolsByIds(List<Long> ids, OshUser operator) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("请选择要删除的工具");
        }
        oshToolMapper.deleteToolsByIds(ids, operator.getUsername());
        for (Long id : ids) {
            oshToolTagMapper.softDeleteRelationsByToolId(id, operator.getUsername());
        }
    }

    @Override
    public OshTool getToolDetail(Long toolId, Long userId) {
        OshTool tool = oshToolMapper.selectToolById(toolId);
        if (tool == null) {
            return null;
        }
        tool.setTags(oshToolTagMapper.selectTagNamesByToolId(toolId));
        tool.setCollectionFlag(resolveCollectionFlag(toolId, userId));
        tool.setLogoUrl(resolveLogoUrl(tool.getLogoUrl()));
        return tool;
    }

    private OshTool buildTool(ToolSaveRequest request, String operator) {
        validateAccessTarget(request);
        OshTool tool = new OshTool();
        tool.setToolName(request.getToolName());
        tool.setDescription(request.getDescription());
        tool.setLogoUrl(request.getLogoUrl());
        tool.setAccessType(request.getId() == null && request.getAccessType() == null ? ACCESS_TYPE_INTERNAL : request.getAccessType());
        tool.setRoutePath(request.getRoutePath());
        tool.setIframeUrl(request.getIframeUrl());
        tool.setGithubUrl(request.getGithubUrl());
        tool.setPrice(request.getId() == null && request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());
        tool.setOriginalPrice(request.getId() == null && request.getOriginalPrice() == null ? BigDecimal.ZERO : request.getOriginalPrice());
        tool.setPointCost(request.getId() == null && request.getPointCost() == null ? 0 : request.getPointCost());
        tool.setStatus(request.getId() == null && request.getStatus() == null ? 1 : request.getStatus());
        tool.setRemark(request.getRemark());
        tool.setResourceType(request.getId() == null ? StringUtils.defaultIfBlank(request.getResourceType(), DEFAULT_RESOURCE_TYPE) : request.getResourceType());
        tool.setLevel(request.getId() == null && request.getLevel() == null ? 1 : request.getLevel());
        tool.setCreateBy(operator);
        tool.setUpdateBy(operator);
        return tool;
    }

    private void validateAccessTarget(ToolSaveRequest request) {
        Integer accessType = request.getAccessType() == null ? ACCESS_TYPE_INTERNAL : request.getAccessType();
        if (request.getId() != null && request.getAccessType() == null) {
            return;
        }
        if (ACCESS_TYPE_INTERNAL == accessType && StringUtils.isBlank(request.getRoutePath())) {
            throw new IllegalArgumentException("站内工具前端路由不能为空");
        }
        if (ACCESS_TYPE_IFRAME == accessType && StringUtils.isBlank(request.getIframeUrl())) {
            throw new IllegalArgumentException("第三方iframe地址不能为空");
        }
    }

    private void validateToolExists(Long toolId) {
        OshTool tool = oshToolMapper.selectToolById(toolId);
        if (tool == null) {
            throw new ServiceException("工具不存在");
        }
    }

    private void syncToolTags(Long toolId, List<String> tags, String operator) {
        oshToolTagMapper.softDeleteRelationsByToolId(toolId, operator);
        List<String> normalizedTags = tags == null ? Collections.emptyList() : tags;
        for (String tagName : normalizedTags) {
            OshToolTag tag = getOrCreateTag(tagName, operator);
            oshToolTagMapper.insertToolTagRel(toolId, tag.getId(), operator);
        }
    }

    private OshToolTag getOrCreateTag(String tagName, String operator) {
        OshToolTag tag = oshToolTagMapper.selectByName(tagName);
        if (tag != null) {
            if (!Integer.valueOf(0).equals(tag.getDeleteFlag()) || !Integer.valueOf(1).equals(tag.getStatus())) {
                oshToolTagMapper.activateToolTag(tag.getId(), operator);
            }
            return tag;
        }
        OshToolTag newTag = new OshToolTag();
        newTag.setName(tagName);
        newTag.setCreateBy(operator);
        newTag.setUpdateBy(operator);
        if (oshToolTagMapper.insertToolTag(newTag) <= 0) {
            throw new ServiceException("新增工具标签失败");
        }
        return newTag;
    }

    private Integer resolveCollectionFlag(Long toolId, Long userId) {
        if (userId == null) {
            return 0;
        }
        List<Long> ids = oshToolCollectionMapper.selectActiveToolIdsByUserIdAndToolIds(userId, Collections.singletonList(toolId));
        return ids.contains(toolId) ? 1 : 0;
    }

    private String resolveLogoUrl(String logoUrl) {
        if (StringUtils.isBlank(logoUrl)) {
            return logoUrl;
        }
        if (StringUtils.startsWithIgnoreCase(logoUrl, "http://") || StringUtils.startsWithIgnoreCase(logoUrl, "https://")) {
            return logoUrl;
        }
        return ossService.getLimitedUrl(logoUrl, LOGO_URL_EXPIRE_MINUTES);
    }
}
