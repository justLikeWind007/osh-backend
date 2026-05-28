package com.backstage.system.service.impl.tool;

import com.backstage.common.response.PageResponse;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.controller.course.OshCourseController;
import com.backstage.system.domain.tool.OshTool;
import com.backstage.system.domain.tool.OshToolPackage;
import com.backstage.system.mapper.tool.OshToolCollectionMapper;
import com.backstage.system.mapper.tool.OshToolEsMapper;
import com.backstage.system.mapper.tool.OshToolMapper;
import com.backstage.system.mapper.tool.OshToolPackageMapper;
import com.backstage.system.mapper.tool.OshToolTagMapper;
import com.backstage.system.request.tool.ToolSearchRequest;
import com.backstage.system.service.tool.IOshToolEsService;
import com.backstage.system.service.tool.ToolIndexEventType;
import com.backstage.system.service.tool.ToolIndexMessage;
import com.backstage.system.service.tool.ToolIndexPackageMessage;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OshToolEsServiceImpl implements IOshToolEsService {

    private static final int PAGE_SIZE = 200;

    private static final Logger log = LoggerFactory.getLogger(OshToolEsServiceImpl.class);

    @Autowired
    private OshToolEsMapper oshToolEsMapper;

    @Autowired
    private OshToolMapper oshToolMapper;

    @Autowired
    private OshToolTagMapper oshToolTagMapper;

    @Autowired
    private OshToolPackageMapper oshToolPackageMapper;

    @Autowired
    private OshToolCollectionMapper oshToolCollectionMapper;

    @Override
    public PageResponse<OshTool> searchTools(ToolSearchRequest request, Long userId) {
        try {
            normalizeSearchRequest(request);
            PageResponse<OshTool> response;
            if (Integer.valueOf(1).equals(request.getCollectionFlag())) {
                response = searchCollectedTools(request, userId);
            } else {
                response = oshToolEsMapper.searchTools(request, null);
            }
            fillUserState(response.getRows(), userId);
            log.debug("从es中查出: {} 条数据", response.getRows().size());
            return response;
        } catch (Exception ex) {
            throw new IllegalStateException("search tools from es failed", ex);
        }
    }

    @Override
    public int syncAllToolsToEs() {
        int pageNum = 1;
        int total = 0;
        try {
            oshToolEsMapper.deleteAllTools();
            while (true) {
                PageHelper.startPage(pageNum, PAGE_SIZE);
                List<OshTool> rows = oshToolMapper.selectAllToolsForEsSync();
                if (StringUtils.isEmpty(rows)) {
                    break;
                }
                List<ToolIndexMessage> documents = new ArrayList<>(rows.size());
                for (OshTool row : rows) {
                    ToolIndexMessage message = buildIndexMessage(row, ToolIndexEventType.TOOL_INDEX_UPDATE);
                    if (message != null) {
                        documents.add(message);
                    }
                }
                total += oshToolEsMapper.bulkUpsertTools(documents);
                if (rows.size() < PAGE_SIZE) {
                    break;
                }
                pageNum++;
            }
            return total;
        } catch (Exception ex) {
            throw new IllegalStateException("sync tools to es failed", ex);
        }
    }

    @Override
    public ToolIndexMessage buildIndexMessage(Long toolId, String eventType) {
        OshTool tool = oshToolMapper.selectToolById(toolId);
        if (tool == null) {
            return null;
        }
        return buildIndexMessage(tool, eventType);
    }

    private ToolIndexMessage buildIndexMessage(OshTool tool, String eventType) {
        if (tool == null) {
            return null;
        }
        List<String> tagNames = oshToolTagMapper.selectTagNamesByToolId(tool.getId());
        List<Long> tagIds = oshToolTagMapper.selectTagIdsByToolId(tool.getId());
        List<OshToolPackage> packages = oshToolPackageMapper.selectPackagesByToolId(tool.getId());
        return buildIndexMessage(tool, tagIds, tagNames, packages, eventType);
    }

    private PageResponse<OshTool> searchCollectedTools(ToolSearchRequest request, Long userId) throws Exception {
        if (userId == null) {
            return PageResponse.of(Collections.emptyList(), 0L, request.getPageNum(), request.getPageSize());
        }
        List<Long> collectedToolIds = oshToolCollectionMapper.selectActiveToolIdsByUserId(userId);
        if (StringUtils.isEmpty(collectedToolIds)) {
            return PageResponse.of(Collections.emptyList(), 0L, request.getPageNum(), request.getPageSize());
        }
        ToolSearchRequest collectionRequest = new ToolSearchRequest();
        collectionRequest.setPageNum(1);
        collectionRequest.setPageSize(collectedToolIds.size());
        collectionRequest.setKeyword(request.getKeyword());
        collectionRequest.setNo(request.getNo());
        collectionRequest.setTags(request.getTags());
        collectionRequest.setResourceType(request.getResourceType());
        PageResponse<OshTool> response = oshToolEsMapper.searchTools(collectionRequest, collectedToolIds);
        List<OshTool> orderedRows = sortRowsByCollectedOrder(response.getRows(), collectedToolIds);
        return PageResponse.of(buildPagedRows(orderedRows, request.getPageNum(), request.getPageSize()),
                orderedRows.size(), request.getPageNum(), request.getPageSize());
    }

    private List<OshTool> sortRowsByCollectedOrder(List<OshTool> rows, List<Long> collectedToolIds) {
        if (StringUtils.isEmpty(rows)) {
            return Collections.emptyList();
        }
        return rows.stream()
                .sorted(Comparator.comparingInt(row -> collectedToolIds.indexOf(row.getId())))
                .collect(Collectors.toList());
    }

    private List<OshTool> buildPagedRows(List<OshTool> rows, int pageNum, int pageSize) {
        int fromIndex = Math.max(0, (pageNum - 1) * pageSize);
        if (fromIndex >= rows.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(rows.size(), fromIndex + pageSize);
        return rows.subList(fromIndex, toIndex);
    }

    private void normalizeSearchRequest(ToolSearchRequest request) {
        if (request == null) {
            return;
        }
        if (StringUtils.isNotEmpty(request.getNo())) {
            request.setToolId(null);
            request.setKeyword(null);
            request.setTags(null);
            request.setResourceType(null);
            request.setIsFollowing(false);
            request.setCollectionFlag(null);
        }
    }

    private void fillUserState(List<OshTool> rows, Long userId) {
        if (StringUtils.isEmpty(rows) || userId == null) {
            return;
        }
        List<Long> toolIds = rows.stream().map(OshTool::getId).collect(Collectors.toList());
        List<Long> collectedIds = oshToolCollectionMapper.selectActiveToolIdsByUserIdAndToolIds(userId, toolIds);
        for (OshTool row : rows) {
            row.setCollectionFlag(collectedIds.contains(row.getId()) ? 1 : 0);
            Integer remainingCount = oshToolMapper.selectUserRemainingCount(row.getId(), userId);
            int value = remainingCount == null ? 0 : remainingCount;
            row.setRemainingCount(value);
            row.setPurchasedFlag(value > 0 ? 1 : 0);
        }
    }

    private ToolIndexMessage buildIndexMessage(OshTool tool, List<Long> tagIds, List<String> tagNames,
                                               List<OshToolPackage> packages, String eventType) {
        ToolIndexMessage message = new ToolIndexMessage();
        message.setEventType(eventType);
        message.setId(tool.getId());
        message.setToolName(tool.getToolName());
        message.setNo(tool.getNo());
        message.setDescription(tool.getDescription());
        message.setRoutePath(tool.getRoutePath());
        message.setGithubUrl(tool.getGithubUrl());
        message.setResourceType(tool.getResourceType());
        message.setLevel(tool.getLevel());
        message.setStatus(tool.getStatus());
        message.setDeleteFlag(tool.getDeleteFlag() == null ? 0 : tool.getDeleteFlag());
        message.setTagIds(tagIds == null ? Collections.emptyList() : tagIds);
        message.setTagNames(tagNames == null ? Collections.emptyList() : tagNames);
        message.setTagNamesText(String.join(" ", message.getTagNames()));
        message.setPackages(toPackageMessages(packages));
        message.setPackageCount(message.getPackages().size());
        fillMinPackage(message);
        message.setViewCount(tool.getViewCount() == null ? 0L : tool.getViewCount());
        message.setTotalUsage(tool.getTotalUsage() == null ? 0L : tool.getTotalUsage());
        message.setCollectionCount(tool.getCollectionCount() == null ? 0 : tool.getCollectionCount());
        message.setGoodCount(tool.getGoodCount() == null ? 0 : tool.getGoodCount());
        message.setBadCount(tool.getBadCount() == null ? 0 : tool.getBadCount());
        message.setHotScore(calculateHotScore(message));
        message.setCreateBy(tool.getCreateBy());
        message.setUpdateBy(tool.getUpdateBy());
        message.setCreateTime(tool.getCreateTime());
        message.setUpdateTime(tool.getUpdateTime());
        message.setSearchText(buildSearchText(message));
        return message;
    }

    private List<ToolIndexPackageMessage> toPackageMessages(List<OshToolPackage> packages) {
        if (StringUtils.isEmpty(packages)) {
            return Collections.emptyList();
        }
        List<ToolIndexPackageMessage> messages = new ArrayList<>();
        for (OshToolPackage item : packages) {
            ToolIndexPackageMessage message = new ToolIndexPackageMessage();
            message.setId(item.getId());
            message.setPackageName(item.getPackageName());
            message.setUseCount(item.getUseCount());
            message.setPrice(item.getPrice());
            message.setPointCost(item.getPointCost());
            message.setPayType(item.getPayType());
            message.setStatus(item.getStatus());
            message.setSortOrder(item.getSortOrder());
            messages.add(message);
        }
        return messages;
    }

    private void fillMinPackage(ToolIndexMessage message) {
        if (StringUtils.isEmpty(message.getPackages())) {
            message.setMinPackagePrice(BigDecimal.ZERO);
            message.setMinPackageUseCount(0);
            return;
        }
        ToolIndexPackageMessage first = message.getPackages().stream()
                .filter(item -> Integer.valueOf(1).equals(item.getStatus()))
                .sorted(Comparator.comparing(ToolIndexPackageMessage::getSortOrder,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .findFirst()
                .orElse(message.getPackages().get(0));
        message.setMinPackagePrice(first.getPrice() == null ? BigDecimal.ZERO : first.getPrice());
        message.setMinPackageUseCount(first.getUseCount() == null ? 0 : first.getUseCount());
    }

    private Double calculateHotScore(ToolIndexMessage message) {
        return message.getViewCount() * 0.2
                + message.getTotalUsage() * 2.0
                + message.getCollectionCount() * 3.0
                + message.getGoodCount() * 1.5
                - message.getBadCount();
    }

    private String buildSearchText(ToolIndexMessage message) {
        StringBuilder builder = new StringBuilder();
        appendSearchField(builder, message.getToolName());
        appendSearchField(builder, message.getNo());
        appendSearchField(builder, message.getDescription());
        appendSearchField(builder, message.getTagNamesText());
        return builder.toString().trim();
    }

    private void appendSearchField(StringBuilder builder, String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(value.trim());
    }
}
