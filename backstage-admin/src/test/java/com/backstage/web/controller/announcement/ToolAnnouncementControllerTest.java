package com.backstage.web.controller.announcement;

import com.backstage.system.controller.announcement.ToolAnnouncementController;
import com.backstage.system.domain.vo.announcement.ToolAnnouncementVO;
import com.backstage.system.service.announcement.IToolAnnouncementService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ToolAnnouncementControllerTest {

    private MockMvc mockMvc;
    private IToolAnnouncementService toolAnnouncementService;

    @Before
    public void setUp() {
        ToolAnnouncementController controller = new ToolAnnouncementController();
        toolAnnouncementService = mock(IToolAnnouncementService.class);
        ReflectionTestUtils.setField(controller, "toolAnnouncementService", toolAnnouncementService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnLatestToolAnnouncements() throws Exception {
        ToolAnnouncementVO announcement = new ToolAnnouncementVO();
        announcement.setId(1L);
        announcement.setTitle("工具公告");
        announcement.setLink("https://example.com/tool");
        announcement.setCreateTime(LocalDateTime.of(2026, 5, 19, 10, 0, 0));
        when(toolAnnouncementService.listLatestToolAnnouncements()).thenReturn(Collections.singletonList(announcement));

        mockMvc.perform(get("/pc/announcement/tool/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("工具公告"))
                .andExpect(jsonPath("$.data[0].link").value("https://example.com/tool"));

        verify(toolAnnouncementService).listLatestToolAnnouncements();
    }
}
