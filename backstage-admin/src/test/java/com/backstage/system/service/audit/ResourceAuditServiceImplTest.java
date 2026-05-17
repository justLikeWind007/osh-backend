package com.backstage.system.service.audit;

import com.backstage.common.enums.ResourceTypeEnum;
import com.backstage.system.domain.audit.ResourceAuditRequest;
import com.backstage.system.mapper.audit.ResourceAuditMapper;
import com.backstage.system.service.impl.audit.ResourceAuditServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceAuditServiceImplTest {

    @InjectMocks
    private ResourceAuditServiceImpl resourceAuditService;

    @Mock
    private ResourceAuditMapper resourceAuditMapper;

    @Test
    public void shouldPagePendingResourcesByResourceTypeEnumTableName() {
        ResourceAuditRequest request = new ResourceAuditRequest();
        request.setResourceType("tool");
        request.setPageNum(1);
        request.setPageSize(10);

        when(resourceAuditMapper.selectPendingList(ResourceTypeEnum.TOOL.getTableName(), 0, 10))
                .thenReturn(Collections.emptyList());
        when(resourceAuditMapper.countPending(ResourceTypeEnum.TOOL.getTableName()))
                .thenReturn(0L);

        resourceAuditService.pagePending(request);

        verify(resourceAuditMapper).selectPendingList("osh_tool", 0, 10);
        verify(resourceAuditMapper).countPending("osh_tool");
    }

    @Test
    public void shouldApprovePendingResourceToStatusOne() {
        when(resourceAuditMapper.approvePending("osh_tool", 10001L, "admin", 9L)).thenReturn(1);

        int rows = resourceAuditService.approve("tool", 10001L, "admin", 9L);

        assertEquals(1, rows);
        verify(resourceAuditMapper).approvePending("osh_tool", 10001L, "admin", 9L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUnknownResourceType() {
        resourceAuditService.approve("unknown", 1L, "admin", 9L);
    }
}
