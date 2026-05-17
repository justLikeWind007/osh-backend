package com.backstage.system.service.audit;

import com.backstage.system.domain.audit.ResourceAuditPageVO;
import com.backstage.system.domain.audit.ResourceAuditRequest;

public interface IResourceAuditService {

    ResourceAuditPageVO pagePending(ResourceAuditRequest request);

    int approve(String resourceType, Long resourceId, String operator, Long operatorId);
}
