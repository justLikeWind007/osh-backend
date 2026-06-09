package com.backstage.web.controller.tool;

import com.backstage.system.controller.tool.OshToolController;
import com.backstage.system.service.tool.IOshToolCollectionService;
import com.backstage.system.service.tool.IOshToolEsService;
import com.backstage.system.service.tool.IOshToolService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OshToolControllerEsSyncTest {

    private MockMvc mockMvc;
    private IOshToolEsService oshToolEsService;

    @Before
    public void setUp() {
        OshToolController controller = new OshToolController();
        ReflectionTestUtils.setField(controller, "oshToolService", mock(IOshToolService.class));
        ReflectionTestUtils.setField(controller, "oshToolCollectionService", mock(IOshToolCollectionService.class));
        oshToolEsService = mock(IOshToolEsService.class);
        ReflectionTestUtils.setField(controller, "oshToolEsService", oshToolEsService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldSyncAllToolsToEs() throws Exception {
        when(oshToolEsService.syncAllToolsToEs()).thenReturn(15);

        mockMvc.perform(post("/pc/tool/esSync/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(15));

        verify(oshToolEsService).syncAllToolsToEs();
    }

    @Test
    public void shouldFillMissingToolNo() throws Exception {
        IOshToolService oshToolService = mock(IOshToolService.class);
        OshToolController controller = new OshToolController();
        ReflectionTestUtils.setField(controller, "oshToolService", oshToolService);
        ReflectionTestUtils.setField(controller, "oshToolCollectionService", mock(IOshToolCollectionService.class));
        ReflectionTestUtils.setField(controller, "oshToolEsService", mock(IOshToolEsService.class));
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        when(oshToolService.fillMissingToolNo()).thenReturn(7);

        mockMvc.perform(post("/pc/tool/fill/no"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(7));

        verify(oshToolService).fillMissingToolNo();
    }
}
