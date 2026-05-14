package com.backstage.system.service.audit;

import com.backstage.system.domain.audit.ResourceAuditPageVO;
import com.backstage.system.domain.audit.ResourceAuditRequest;

public interface IResourceAuditService {

    ResourceAuditPageVO pagePending(ResourceAuditRequest request);

    int audit(String resourceType, Long resourceId, Integer status, String operator, Long operatorId);
}
