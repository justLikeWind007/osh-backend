package com.backstage.system.service.impl.audit;

import com.backstage.common.enums.ResourceTypeEnum;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.audit.ResourceAuditPageVO;
import com.backstage.system.domain.audit.ResourceAuditRequest;
import com.backstage.system.mapper.audit.ResourceAuditMapper;
import com.backstage.system.service.audit.IResourceAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceAuditServiceImpl implements IResourceAuditService {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    @Autowired
    private ResourceAuditMapper resourceAuditMapper;

    @Override
    public ResourceAuditPageVO pagePending(ResourceAuditRequest request) {
        ResourceTypeEnum resourceType = parseResourceType(request.getResourceType());
        int pageNum = normalizePageNum(request.getPageNum());
        int pageSize = normalizePageSize(request.getPageSize());
        int offset = (pageNum - 1) * pageSize;

        ResourceAuditPageVO page = new ResourceAuditPageVO();
        page.setRows(resourceAuditMapper.selectPendingList(resourceType.getTableName(), offset, pageSize));
        page.setTotal(resourceAuditMapper.countPending(resourceType.getTableName()));
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approve(String resourceType, Long resourceId, String operator, Long operatorId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        ResourceTypeEnum typeEnum = parseResourceType(resourceType);
        int rows = resourceAuditMapper.approvePending(typeEnum.getTableName(), resourceId, operator, operatorId);
        if (rows <= 0) {
            throw new ServiceException("待审核资源不存在或已处理");
        }
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
}
