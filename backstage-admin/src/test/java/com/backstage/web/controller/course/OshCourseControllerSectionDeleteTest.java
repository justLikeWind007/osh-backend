package com.backstage.web.controller.course;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.controller.course.OshCourseController;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.service.IOshCourseCollectionService;
import com.backstage.system.service.IOshCourseQuestionService;
import com.backstage.system.service.IOshCourseService;
import com.backstage.system.service.course.IOshCourseEsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OshCourseControllerSectionDeleteTest {

    private MockMvc mockMvc;
    private IOshCourseService oshCourseService;
    private OshUser currentUser;

    @Before
    public void setUp() {
        OshCourseController controller = new OshCourseController();
        oshCourseService = mock(IOshCourseService.class);

        ReflectionTestUtils.setField(controller, "oshCourseService", oshCourseService);
        ReflectionTestUtils.setField(controller, "oshCourseEsService", mock(IOshCourseEsService.class));
        ReflectionTestUtils.setField(controller, "oshCourseQuestionService", mock(IOshCourseQuestionService.class));
        ReflectionTestUtils.setField(controller, "oshCourseCollectionService", mock(IOshCourseCollectionService.class));

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        currentUser = new OshUser();
        currentUser.setId(100L);
        currentUser.setUsername("tester");
        ThreadLocalUtil.set(OshUserConstants.USER_INFO, currentUser);
    }

    @After
    public void tearDown() {
        ThreadLocalUtil.remove();
    }

    @Test
    public void shouldDeleteSectionByCourseIdAndSectionId() throws Exception {
        when(oshCourseService.safeDeleteSection(eq(12L), eq(34L), eq(currentUser))).thenReturn(true);

        mockMvc.perform(post("/pc/course/sectionDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseId\":12,\"sectionId\":34}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("操作成功"));

        verify(oshCourseService).safeDeleteSection(12L, 34L, currentUser);
    }
}
