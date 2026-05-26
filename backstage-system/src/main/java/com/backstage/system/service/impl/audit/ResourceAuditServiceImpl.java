package com.backstage.system.service.impl.audit;

import com.backstage.common.async.AsyncExecutorNames;
import com.backstage.common.async.AsyncTaskSupport;
import com.backstage.common.enums.ResourceStatusEnum;
import com.backstage.common.enums.ResourceTypeEnum;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.response.PageResponse;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.config.properties.SearchEsProperties;
import com.backstage.system.domain.audit.ResourceAuditItemVO;
import com.backstage.system.domain.audit.ResourceAuditPageVO;
import com.backstage.system.domain.audit.ResourceAuditRequest;
import com.backstage.system.mapper.audit.ResourceAuditEsMapper;
import com.backstage.system.mapper.audit.ResourceAuditMapper;
import com.backstage.system.service.OutboxEventService;
import com.backstage.system.service.audit.ResourceAuditCallbackContext;
import com.backstage.system.service.audit.ResourceAuditCallbackHandlerRegistry;
import com.backstage.system.service.audit.AuditIndexEventType;
import com.backstage.system.service.audit.AuditIndexMessage;
import com.backstage.system.service.audit.IResourceAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Service
public class ResourceAuditServiceImpl implements IResourceAuditService {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private static final Logger log = LoggerFactory.getLogger(ResourceAuditServiceImpl.class);

    @Autowired
    private ResourceAuditMapper resourceAuditMapper;

    @Autowired
    private ResourceAuditEsMapper resourceAuditEsMapper;

    @Autowired
    private SearchEsProperties searchEsProperties;

    @Autowired
    private OutboxEventService outboxEventService;

    @Autowired
    private ResourceAuditCallbackHandlerRegistry resourceAuditCallbackHandlerRegistry;

    @Resource
    private AsyncTaskSupport asyncTaskSupport;

    @Resource
    @Qualifier(AsyncExecutorNames.NOTIFICATION)
    private Executor notificationTaskExecutor;

    @Override
    public ResourceAuditPageVO pagePending(ResourceAuditRequest request) {
        ResourceTypeEnum resourceType = parseResourceType(request.getResourceType());
        int pageNum = normalizePageNum(request.getPageNum());
        int pageSize = normalizePageSize(request.getPageSize());
        int offset = (pageNum - 1) * pageSize;
        String keyword = normalizeKeyword(request.getKeyword());

        ResourceAuditPageVO page = new ResourceAuditPageVO();
        if (searchEsProperties.isEnabled() && resourceAuditEsMapper.supports(resourceType)) {
            PageResponse<ResourceAuditItemVO> esPage = searchPendingFromEs(resourceType, keyword, pageNum, pageSize);
            page.setRows(esPage.getRows());
            page.setTotal(esPage.getTotal());
            log.debug("ES 搜索命中，共 {} 条", esPage.getTotal());
        } else {
            page.setRows(resourceAuditMapper.selectPendingList(resourceType.getMysqlTableName(), offset, pageSize, keyword));
            page.setTotal(resourceAuditMapper.countPending(resourceType.getMysqlTableName(), keyword));
        }
        page.setPendingTotal(resolvePendingTotal(resourceType));
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int audit(String resourceType, Long resourceId, Integer status, String operator, Long operatorId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        if (!ResourceStatusEnum.isPublished(status) && !ResourceStatusEnum.isOffShelf(status)) {
            throw new IllegalArgumentException("审核状态错误");
        }
        ResourceTypeEnum typeEnum = parseResourceType(resourceType);
        int resourceStatus = status;
        int rows = resourceAuditMapper.updateAuditStatus(typeEnum.getMysqlTableName(), resourceId, resourceStatus, operator, operatorId);
        if (rows <= 0) {
            throw new ServiceException("待审核资源不存在或已处理");
        }
        syncAuditResourceToEs(typeEnum, resourceId, resourceStatus, operator);
        triggerAuditCallbacks(typeEnum, resourceId, resourceStatus, operator, operatorId);
        return rows;
    }

    private ResourceTypeEnum parseResourceType(String resourceType) {
        try {
            return ResourceTypeEnum.fromTypeCode(resourceType);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("资源类型不支持");
        }
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum <= 0 ? DEFAULT_PAGE_NUM : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private String normalizeKeyword(String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            return null;
        }
        String trimmedKeyword = keyword.trim();
        return trimmedKeyword.isEmpty() ? null : trimmedKeyword;
    }

    private PageResponse<ResourceAuditItemVO> searchPendingFromEs(ResourceTypeEnum resourceType,
                                                                  String keyword,
                                                                  int pageNum,
                                                                  int pageSize) {
        try {
            return resourceAuditEsMapper.searchPending(resourceType, keyword, pageNum, pageSize);
        } catch (Exception ex) {
            throw new ServiceException("ES查询待审核资源失败：" + ex.getMessage());
        }
    }

    private Long resolvePendingTotal(ResourceTypeEnum resourceType) {
        return resourceAuditMapper.countPending(resourceType.getMysqlTableName(), null);
    }

    private void syncAuditResourceToEs(ResourceTypeEnum resourceType, Long resourceId, int resourceStatus, String operator) {
        if (StringUtils.isEmpty(resourceType.getEsIndexName())) {
            // 该资源类型暂无 ES 索引，跳过同步
            return;
        }
        String eventType = ResourceStatusEnum.isPublished(resourceStatus) ? AuditIndexEventType.AUDIT_APPROVED : AuditIndexEventType.AUDIT_REJECTED;
        AuditIndexMessage message = new AuditIndexMessage();
        message.setEventType(eventType);
        message.setResourceType(resourceType.getType());
        message.setId(resourceId);
        message.setStatus(resourceStatus);
        message.setUpdateBy(operator);
        message.setUpdateTime(java.time.LocalDateTime.now());
        outboxEventService.saveAuditIndexEvent(resourceType, message, operator);
    }

    private void triggerAuditCallbacks(ResourceTypeEnum resourceType,
                                       Long resourceId,
                                       Integer resourceStatus,
                                       String operator,
                                       Long operatorId) {
        ResourceAuditCallbackContext context = new ResourceAuditCallbackContext();
        context.setResourceType(resourceType);
        context.setResourceId(resourceId);
        context.setResourceStatus(resourceStatus);
        context.setOperator(operator);
        context.setOperatorId(operatorId);
        asyncTaskSupport.runAsync(() -> resourceAuditCallbackHandlerRegistry.handle(context), notificationTaskExecutor)
                .exceptionally(ex -> {
                    log.error("审核回调异步执行失败, resourceType={}, resourceId={}, error={}",
                            resourceType.getType(), resourceId, ex.getMessage(), ex);
                    return null;
                });
    }
}
