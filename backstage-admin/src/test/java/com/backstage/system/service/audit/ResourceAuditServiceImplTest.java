package com.backstage.system.service.audit;

import com.backstage.common.enums.ResourceTypeEnum;
import com.backstage.common.response.PageResponse;
import com.backstage.system.config.properties.SearchEsProperties;
import com.backstage.system.domain.audit.ResourceAuditItemVO;
import com.backstage.system.domain.audit.ResourceAuditPageVO;
import com.backstage.system.domain.audit.ResourceAuditRequest;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.audit.ResourceAuditEsMapper;
import com.backstage.system.mapper.audit.ResourceAuditMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.OutboxEventService;
import com.backstage.system.service.impl.audit.ResourceAuditServiceImpl;
import com.backstage.system.service.IOshCourseService;
import com.backstage.system.service.course.CourseIndexEventType;
import com.backstage.system.service.course.CourseIndexUpsertMessage;
import com.backstage.system.service.tool.IOshToolEsService;
import com.backstage.system.service.tool.ToolIndexEventType;
import com.backstage.system.service.tool.ToolIndexMessage;
import com.backstage.system.service.websocket.WebSocketNotifyService;
import com.backstage.system.domain.websocket.WsNotifyMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceAuditServiceImplTest {

    @InjectMocks
    private ResourceAuditServiceImpl resourceAuditService;

    @Mock
    private ResourceAuditMapper resourceAuditMapper;

    @Mock
    private ResourceAuditEsMapper resourceAuditEsMapper;

    @Mock
    private IOshToolEsService oshToolEsService;

    @Mock
    private OutboxEventService outboxEventService;

    @Mock
    private IOshCourseService oshCourseService;

    @Mock
    private OshUserMapper oshUserMapper;

    @Mock
    private WebSocketNotifyService webSocketNotifyService;

    private SearchEsProperties searchEsProperties;

    @Before
    public void setUp() {
        searchEsProperties = new SearchEsProperties();
        searchEsProperties.setEnabled(false);
        ReflectionTestUtils.setField(resourceAuditService, "searchEsProperties", searchEsProperties);
    }

    @Test
    public void shouldPagePendingResourcesByResourceTypeEnumTableName() {
        ResourceAuditRequest request = new ResourceAuditRequest();
        request.setResourceType("open_project");
        request.setPageNum(1);
        request.setPageSize(10);

        when(resourceAuditMapper.selectPendingList(ResourceTypeEnum.OPEN_PROJECT.getTableName(), 0, 10, null))
                .thenReturn(Collections.emptyList());
        when(resourceAuditMapper.countPending(ResourceTypeEnum.OPEN_PROJECT.getTableName(), null))
                .thenReturn(0L);

        resourceAuditService.pagePending(request);

        verify(resourceAuditMapper).selectPendingList("osh_open_project", 0, 10, null);
        verify(resourceAuditMapper).countPending("osh_open_project", null);
    }

    @Test
    public void shouldUseEsWhenEnabledAndCourseAuditSearchSupported() throws Exception {
        searchEsProperties.setEnabled(true);
        ResourceAuditRequest request = new ResourceAuditRequest();
        request.setResourceType("course");
        request.setPageNum(2);
        request.setPageSize(10);
        request.setKeyword("Vue");

        when(resourceAuditEsMapper.supports(ResourceTypeEnum.COURSE)).thenReturn(true);
        when(resourceAuditEsMapper.searchPending(ResourceTypeEnum.COURSE, "Vue", 2, 10))
                .thenReturn(PageResponse.of(Collections.singletonList(new ResourceAuditItemVO()), 1L, 2, 10));
        when(resourceAuditMapper.countPending(ResourceTypeEnum.COURSE.getTableName(), null)).thenReturn(5L);

        ResourceAuditPageVO page = resourceAuditService.pagePending(request);

        assertEquals(Long.valueOf(1L), page.getTotal());
        assertEquals(Long.valueOf(5L), page.getPendingTotal());

        verify(resourceAuditEsMapper).searchPending(ResourceTypeEnum.COURSE, "Vue", 2, 10);
        verify(resourceAuditMapper).countPending("osh_course", null);
    }

    @Test
    public void shouldUseMysqlWhenEsDisabledAndPassKeyword() {
        ResourceAuditRequest request = new ResourceAuditRequest();
        request.setResourceType("tool");
        request.setPageNum(1);
        request.setPageSize(20);
        request.setKeyword("计算器");

        when(resourceAuditMapper.selectPendingList(ResourceTypeEnum.TOOL.getTableName(), 0, 20, "计算器"))
                .thenReturn(Collections.emptyList());
        when(resourceAuditMapper.countPending(ResourceTypeEnum.TOOL.getTableName(), "计算器"))
                .thenReturn(0L);
        when(resourceAuditMapper.countPending(ResourceTypeEnum.TOOL.getTableName(), null))
                .thenReturn(3L);

        resourceAuditService.pagePending(request);

        verify(resourceAuditMapper).selectPendingList("osh_tool", 0, 20, "计算器");
        verify(resourceAuditMapper).countPending("osh_tool", "计算器");
        verify(resourceAuditMapper).countPending("osh_tool", null);
        verifyNoInteractions(resourceAuditEsMapper);
    }

    @Test
    public void shouldApprovePendingResourceToStatusFour() {
        when(resourceAuditMapper.updateAuditStatus("osh_open_project", 10001L, 4, "admin", 9L))
                .thenReturn(1);

        int rows = resourceAuditService.audit("open_project", 10001L, 1, "admin", 9L);

        assertEquals(1, rows);
        verify(resourceAuditMapper).updateAuditStatus("osh_open_project", 10001L, 4, "admin", 9L);
    }

    @Test
    public void shouldRejectPendingResourceToStatusSix() {
        when(resourceAuditMapper.updateAuditStatus("osh_open_project", 10001L, 6, "admin", 9L))
                .thenReturn(1);

        int rows = resourceAuditService.audit("open_project", 10001L, 2, "admin", 9L);

        assertEquals(1, rows);
        verify(resourceAuditMapper).updateAuditStatus("osh_open_project", 10001L, 6, "admin", 9L);
    }


    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUnknownResourceType() {
        resourceAuditService.audit("unknown", 1L, 1, "admin", 9L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUnsupportedAuditStatus() {
        resourceAuditService.audit("open_project", 1L, 3, "admin", 9L);
    }
}
