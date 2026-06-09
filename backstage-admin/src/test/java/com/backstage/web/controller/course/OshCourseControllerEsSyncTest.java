package com.backstage.web.controller.course;

import com.backstage.system.controller.course.OshCourseController;
import com.backstage.system.service.IOshCourseCollectionService;
import com.backstage.system.service.IOshCourseQuestionService;
import com.backstage.system.service.IOshCourseService;
import com.backstage.system.service.course.IOshCourseEsService;
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

public class OshCourseControllerEsSyncTest {

    private MockMvc mockMvc;
    private IOshCourseEsService oshCourseEsService;

    @Before
    public void setUp() {
        OshCourseController controller = new OshCourseController();
        ReflectionTestUtils.setField(controller, "oshCourseService", mock(IOshCourseService.class));
        ReflectionTestUtils.setField(controller, "oshCourseQuestionService", mock(IOshCourseQuestionService.class));
        ReflectionTestUtils.setField(controller, "oshCourseCollectionService", mock(IOshCourseCollectionService.class));
        oshCourseEsService = mock(IOshCourseEsService.class);
        ReflectionTestUtils.setField(controller, "oshCourseEsService", oshCourseEsService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldSyncAllCoursesToEsWithoutStatusFilter() throws Exception {
        when(oshCourseEsService.syncAllCoursesToEsWithoutStatusFilter()).thenReturn(12);

        mockMvc.perform(post("/pc/course/esSync/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(12));

        verify(oshCourseEsService).syncAllCoursesToEsWithoutStatusFilter();
    }
}
