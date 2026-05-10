package com.backstage.system.service.impl.tool;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolPackage;
import com.backstage.system.domain.tool.OshToolTag;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.tool.OshToolCollectionMapper;
import com.backstage.system.mapper.tool.OshToolMapper;
import com.backstage.system.mapper.tool.OshToolPackageMapper;
import com.backstage.system.mapper.tool.OshToolTagMapper;
import com.backstage.system.mapper.tool.OshToolVoteMapper;
import com.backstage.system.domain.tool.OshToolVote;
import com.backstage.system.request.tool.ToolPackageSaveRequest;
import com.backstage.system.request.tool.ToolRecommendRequest;
import com.backstage.system.request.tool.ToolSaveRequest;
import com.backstage.system.request.tool.ToolSearchRequest;
import com.backstage.system.service.tool.IOshToolService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OshToolServiceImpl implements IOshToolService {

    private static final int ACCESS_TYPE_INTERNAL = 1;
    private static final int ACCESS_TYPE_IFRAME = 2;
    private static final int MAX_TOOL_TAG_COUNT = 3;
    private static final String DEFAULT_RESOURCE_TYPE = "FREE";
    private static final String RESOURCE_TYPE_CASH_ONLY = "CASH_ONLY";
    private static final String RESOURCE_TYPE_CASH_POINT = "CASH_POINT";
    private static final int PAY_TYPE_CASH = 1;
    private static final int PAY_TYPE_CASH_POINT = 3;
    private static final BigDecimal POINT_EXCHANGE_RATE = BigDecimal.TEN;
    private static final int RECOMMEND_PAGE_SIZE = 5;
    private static final int VOTE_TYPE_GOOD = 1;
    private static final int VOTE_TYPE_BAD = 3;

    @Autowired
    private OshToolMapper oshToolMapper;

    @Autowired
    private OshToolTagMapper oshToolTagMapper;

    @Autowired
    private OshToolPackageMapper oshToolPackageMapper;

    @Autowired
    private OshToolCollectionMapper oshToolCollectionMapper;

    @Autowired
    private OshToolVoteMapper oshToolVoteMapper;

    @Override
    public List<OshTool> pageQuerySearchTool(Long userId, ToolSearchRequest request) {
        normalizeSearchRequest(request);
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<OshTool> list = oshToolMapper.pageQuerySearchTool(request, userId);
        fillToolExtras(list);
        return list;
    }

    @Override
    public List<OshTool> listRecommendTools(Long userId, ToolRecommendRequest request) {
        normalizeRecommendRequest(request);
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<OshTool> list = oshToolMapper.selectRecommendTools(request, userId);
        fillToolExtras(list);
        return list;
    }

    @Override
    public List<OshToolTag> listAvailableTags() {
        return oshToolTagMapper.selectAvailableTags();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTool(ToolSaveRequest request, OshUser operator) {
        if (StringUtils.isBlank(request.getToolName())) {
            throw new IllegalArgumentException("工具名称不能为空");
        }
        validateToolTags(request.getTags());
        OshTool tool = buildTool(request, operator.getUsername());
        if (oshToolMapper.insertTool(tool) <= 0) {
            throw new ServiceException("新增工具失败");
        }
        syncToolTags(tool.getId(), request.getTags(), operator.getUsername());
        if (request.getPackages() != null) {
            syncToolPackages(tool.getId(), resolvePackagesByResourceType(request), request.getResourceType(), operator.getUsername());
        }
        return tool.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateTool(ToolSaveRequest request, OshUser operator) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("工具ID不能为空");
        }
        validateToolTags(request.getTags());
        validateToolExists(request.getId());
        OshTool tool = buildTool(request, operator.getUsername());
        tool.setId(request.getId());
        if (oshToolMapper.updateTool(tool) <= 0) {
            throw new ServiceException("修改工具失败");
        }
        if (request.getTags() != null) {
            syncToolTags(tool.getId(), request.getTags(), operator.getUsername());
        }
        if (request.getPackages() != null) {
            syncToolPackages(tool.getId(), resolvePackagesByResourceType(request), request.getResourceType(), operator.getUsername());
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
        tool.setPackages(oshToolPackageMapper.selectPackagesByToolId(toolId));
        fillUserToolQuota(tool, userId);
        tool.setVoteType(resolveVoteType(toolId, userId));
        return tool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer consumeToolUsage(Long userId, String operator, Long toolId) {
        if (userId == null) {
            throw new IllegalArgumentException("请先登录");
        }
        if (toolId == null) {
            throw new IllegalArgumentException("工具ID不能为空");
        }
        OshTool tool = oshToolMapper.selectToolById(toolId);
        if (tool == null) {
            throw new ServiceException("工具不存在");
        }
        if (!isPackageEnabledResourceType(tool.getResourceType())) {
            return 0;
        }
        if (oshToolMapper.consumeUserToolQuota(toolId, userId, operator) <= 0) {
            throw new ServiceException("工具使用次数不足");
        }
        oshToolMapper.increaseTotalUsage(toolId);
        return oshToolMapper.selectUserRemainingCount(toolId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer voteTool(Long userId, String operator, Long toolId, Integer type) {
        if (userId == null) {
            throw new IllegalArgumentException("请先登录");
        }
        if (toolId == null) {
            throw new IllegalArgumentException("工具ID不能为空");
        }
        if (!Integer.valueOf(VOTE_TYPE_GOOD).equals(type) && !Integer.valueOf(VOTE_TYPE_BAD).equals(type)) {
            throw new IllegalArgumentException("评价类型错误");
        }
        validateToolExists(toolId);

        OshToolVote existVote = oshToolVoteMapper.selectByUserIdAndToolId(userId, toolId);
        if (existVote != null && Integer.valueOf(0).equals(existVote.getDeleteFlag())) {
            if (type.equals(existVote.getType())) {
                oshToolVoteMapper.deleteToolVote(existVote.getId(), operator);
                decreaseVoteCount(toolId, type);
                return 0;
            }
            decreaseVoteCount(toolId, existVote.getType());
            existVote.setType(type);
            existVote.setUpdateBy(operator);
            oshToolVoteMapper.updateToolVote(existVote);
            increaseVoteCount(toolId, type);
            return type;
        }

        if (existVote != null) {
            existVote.setType(type);
            existVote.setUpdateBy(operator);
            oshToolVoteMapper.updateToolVote(existVote);
        } else {
            OshToolVote vote = new OshToolVote();
            vote.setUserId(userId);
            vote.setToolId(toolId);
            vote.setType(type);
            vote.setCreateBy(operator);
            vote.setUpdateBy(operator);
            oshToolVoteMapper.insertToolVote(vote);
        }
        increaseVoteCount(toolId, type);
        return type;
    }

    private OshTool buildTool(ToolSaveRequest request, String operator) {
        validateAccessTarget(request);
        OshTool tool = new OshTool();
        tool.setToolName(request.getToolName());
        tool.setDescription(request.getDescription());
        tool.setLogoUrl(null);
        tool.setAccessType(request.getId() == null && request.getAccessType() == null ? ACCESS_TYPE_INTERNAL : request.getAccessType());
        tool.setRoutePath(request.getRoutePath());
        tool.setIframeUrl(request.getIframeUrl());
        tool.setGithubUrl(request.getGithubUrl());
        tool.setPrice(BigDecimal.ZERO);
        tool.setOriginalPrice(BigDecimal.ZERO);
        tool.setPointCost(request.getId() == null && request.getPointCost() == null ? 0 : request.getPointCost());
        tool.setStatus(request.getId() == null && request.getStatus() == null ? 1 : request.getStatus());
        tool.setRemark(request.getRemark());
        tool.setResourceType(request.getId() == null ? StringUtils.defaultIfBlank(request.getResourceType(), DEFAULT_RESOURCE_TYPE) : request.getResourceType());
        tool.setLevel(request.getId() == null && request.getLevel() == null ? 1 : request.getLevel());
        tool.setCreateBy(operator);
        tool.setUpdateBy(operator);
        return tool;
    }

    private void normalizeSearchRequest(ToolSearchRequest request) {
        if (request.getPageNum() <= 0) {
            request.setPageNum(1);
        }
        if (request.getPageSize() <= 0) {
            request.setPageSize(10);
        }
        if (request.getCollectionFlag() == null && Boolean.TRUE.equals(request.getIsFollowing())) {
            request.setCollectionFlag(1);
        }
    }

    private void normalizeRecommendRequest(ToolRecommendRequest request) {
        if (request.getPageNum() <= 0) {
            request.setPageNum(1);
        }
        request.setPageSize(RECOMMEND_PAGE_SIZE);
        if (!"LATEST".equals(request.getType())) {
            request.setType("HOT");
        }
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

    private void validateToolTags(List<String> tags) {
        if (tags != null && tags.size() > MAX_TOOL_TAG_COUNT) {
            throw new IllegalArgumentException("工具标签最多添加" + MAX_TOOL_TAG_COUNT + "个");
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
            oshToolTagMapper.increaseUseCount(tag.getId());
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

    private void syncToolPackages(Long toolId, List<ToolPackageSaveRequest> packages, String resourceType, String operator) {
        if (packages == null) {
            return;
        }
        oshToolPackageMapper.softDeletePackagesByToolId(toolId, operator);
        if (packages.isEmpty()) {
            return;
        }
        for (ToolPackageSaveRequest request : packages) {
            if (request == null) {
                continue;
            }
            validateToolPackage(request);
            OshToolPackage toolPackage = buildToolPackage(toolId, request, resourceType, operator);
            if (request.getId() == null) {
                if (oshToolPackageMapper.insertToolPackage(toolPackage) <= 0) {
                    throw new ServiceException("新增工具套餐失败");
                }
            } else if (oshToolPackageMapper.updateToolPackage(toolPackage) <= 0) {
                throw new ServiceException("修改工具套餐失败");
            }
        }
    }

    private OshToolPackage buildToolPackage(Long toolId, ToolPackageSaveRequest request, String resourceType, String operator) {
        OshToolPackage toolPackage = new OshToolPackage();
        toolPackage.setId(request.getId());
        toolPackage.setToolId(toolId);
        toolPackage.setPackageName(StringUtils.defaultIfBlank(request.getPackageName(), request.getUseCount() + "次使用套餐"));
        toolPackage.setUseCount(request.getUseCount());
        toolPackage.setPrice(request.getPrice() == null ? BigDecimal.ZERO : request.getPrice());
        toolPackage.setPointCost(calculatePointCost(request.getPrice()));
        toolPackage.setPayType(resolvePackagePayType(resourceType));
        toolPackage.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        toolPackage.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        toolPackage.setCreateBy(operator);
        toolPackage.setUpdateBy(operator);
        return toolPackage;
    }

    private void validateToolPackage(ToolPackageSaveRequest request) {
        if (request.getUseCount() == null || request.getUseCount() <= 0) {
            throw new IllegalArgumentException("套餐使用次数必须大于0");
        }
        if (request.getPrice() != null && request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("套餐价格不能小于0");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("套餐现金金额必须大于0");
        }
    }

    private List<ToolPackageSaveRequest> resolvePackagesByResourceType(ToolSaveRequest request) {
        if (!isPackageEnabledResourceType(request.getResourceType())) {
            return Collections.emptyList();
        }
        return request.getPackages();
    }

    private boolean isPackageEnabledResourceType(String resourceType) {
        return RESOURCE_TYPE_CASH_ONLY.equals(resourceType) || RESOURCE_TYPE_CASH_POINT.equals(resourceType);
    }

    private int calculatePointCost(BigDecimal price) {
        BigDecimal value = price == null ? BigDecimal.ZERO : price;
        return value.multiply(POINT_EXCHANGE_RATE).intValue();
    }

    private int resolvePackagePayType(String resourceType) {
        return RESOURCE_TYPE_CASH_POINT.equals(resourceType) ? PAY_TYPE_CASH_POINT : PAY_TYPE_CASH;
    }

    private void fillToolPackages(List<OshTool> tools) {
        if (tools == null || tools.isEmpty()) {
            return;
        }
        List<Long> toolIds = new ArrayList<>();
        for (OshTool tool : tools) {
            toolIds.add(tool.getId());
        }
        List<OshToolPackage> packages = oshToolPackageMapper.selectPackagesByToolIds(toolIds);
        Map<Long, List<OshToolPackage>> packageMap = new HashMap<>();
        for (OshToolPackage toolPackage : packages) {
            packageMap.computeIfAbsent(toolPackage.getToolId(), key -> new ArrayList<>()).add(toolPackage);
        }
        for (OshTool tool : tools) {
            tool.setPackages(packageMap.getOrDefault(tool.getId(), Collections.emptyList()));
        }
    }

    private void fillToolExtras(List<OshTool> tools) {
        for (OshTool tool : tools) {
            tool.setTags(oshToolTagMapper.selectTagNamesByToolId(tool.getId()));
        }
        fillToolPackages(tools);
    }

    private void fillUserToolQuota(OshTool tool, Long userId) {
        if (userId == null) {
            tool.setRemainingCount(0);
            tool.setPurchasedFlag(0);
            return;
        }
        Integer remainingCount = oshToolMapper.selectUserRemainingCount(tool.getId(), userId);
        int value = remainingCount == null ? 0 : remainingCount;
        tool.setRemainingCount(value);
        tool.setPurchasedFlag(value > 0 ? 1 : 0);
    }

    private Integer resolveCollectionFlag(Long toolId, Long userId) {
        if (userId == null) {
            return 0;
        }
        List<Long> ids = oshToolCollectionMapper.selectActiveToolIdsByUserIdAndToolIds(userId, Collections.singletonList(toolId));
        return ids.contains(toolId) ? 1 : 0;
    }

    private Integer resolveVoteType(Long toolId, Long userId) {
        if (userId == null) {
            return 0;
        }
        Integer voteType = oshToolVoteMapper.selectVoteType(userId, toolId);
        return voteType == null ? 0 : voteType;
    }

    private void increaseVoteCount(Long toolId, Integer type) {
        if (Integer.valueOf(VOTE_TYPE_GOOD).equals(type)) {
            oshToolMapper.increaseGoodCount(toolId);
            return;
        }
        if (Integer.valueOf(VOTE_TYPE_BAD).equals(type)) {
            oshToolMapper.increaseBadCount(toolId);
        }
    }

    private void decreaseVoteCount(Long toolId, Integer type) {
        if (Integer.valueOf(VOTE_TYPE_GOOD).equals(type)) {
            oshToolMapper.decreaseGoodCount(toolId);
            return;
        }
        if (Integer.valueOf(VOTE_TYPE_BAD).equals(type)) {
            oshToolMapper.decreaseBadCount(toolId);
        }
    }

    private boolean isExternalUrl(String url) {
        return StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils.startsWithIgnoreCase(url, "https://");
    }
}
