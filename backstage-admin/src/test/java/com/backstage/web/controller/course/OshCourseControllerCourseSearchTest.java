package com.backstage.web.controller.course;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.response.PageResponse;
import com.backstage.system.controller.course.OshCourseController;
import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.request.CourseCreateRequest;
import com.backstage.system.request.CourseSearchRequest;
import com.backstage.system.service.IOshCourseCollectionService;
import com.backstage.system.service.IOshCourseQuestionService;
import com.backstage.system.service.IOshCourseService;
import com.backstage.system.service.course.IOshCourseEsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OshCourseControllerCourseSearchTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private IOshCourseService oshCourseService;
    private IOshCourseEsService oshCourseEsService;

    @Before
    public void setUp() {
        OshCourseController controller = new OshCourseController();
        objectMapper = new ObjectMapper();
        oshCourseService = mock(IOshCourseService.class);
        oshCourseEsService = mock(IOshCourseEsService.class);

        ReflectionTestUtils.setField(controller, "oshCourseService", oshCourseService);
        ReflectionTestUtils.setField(controller, "oshCourseEsService", oshCourseEsService);
        ReflectionTestUtils.setField(controller, "oshCourseQuestionService", mock(IOshCourseQuestionService.class));
        ReflectionTestUtils.setField(controller, "oshCourseCollectionService", mock(IOshCourseCollectionService.class));

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @org.junit.After
    public void tearDown() {
        ThreadLocalUtil.remove();
    }

    @Test
    public void shouldDelegateAnonymousCourseSearchToEsService() throws Exception {
        CourseSearchLoginVo vo = new CourseSearchLoginVo();
        vo.setId(1001L);
        vo.setTitle("ES课程");
        PageResponse<CourseSearchLoginVo> response = PageResponse.of(Collections.singletonList(vo), 1, 1, 10);
        when(oshCourseEsService.searchCourses(any(CourseSearchRequest.class), eq(null))).thenReturn(response);

        CourseSearchRequest request = new CourseSearchRequest();
        request.setKeyword("ES");
        request.setPageNum(1);
        request.setPageSize(10);

        mockMvc.perform(post("/pc/course/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.rows[0].id").value(1001))
                .andExpect(jsonPath("$.data.rows[0].title").value("ES课程"));

        ArgumentCaptor<CourseSearchRequest> requestCaptor = ArgumentCaptor.forClass(CourseSearchRequest.class);
        verify(oshCourseEsService).searchCourses(requestCaptor.capture(), eq(null));
        verify(oshCourseService, never()).pageQuerySearchCourse(eq(null), any(CourseSearchRequest.class));
        assertEquals("ES", requestCaptor.getValue().getKeyword());
        assertEquals(1, requestCaptor.getValue().getPageNum());
        assertEquals(10, requestCaptor.getValue().getPageSize());
        assertSame(request.getClass(), requestCaptor.getValue().getClass());
    }

    @Test
    public void shouldDelegateFullSyncRequestToEsService() throws Exception {
        when(oshCourseEsService.syncAllCoursesToEs()).thenReturn(12);

        mockMvc.perform(post("/pc/course/esSync/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(12));

        verify(oshCourseEsService, times(1)).syncAllCoursesToEs();
    }

    @Test
    public void shouldAcceptStringTagListWhenSavingCourse() throws Exception {
        OshUser currentUser = new OshUser();
        currentUser.setId(100L);
        currentUser.setUsername("tester");
        ThreadLocalUtil.set(OshUserConstants.USER_INFO, currentUser);
        when(oshCourseService.createCourse(any(CourseCreateRequest.class), eq(currentUser))).thenReturn(321L);

        String requestJson = "{"
                + "\"title\":\"Kafka 课程\","
                + "\"cover\":\"course/cover.png\","
                + "\"intro\":\"课程简介\","
                + "\"price\":19.9,"
                + "\"tPrice\":99.9,"
                + "\"type\":\"media\","
                + "\"resourceType\":\"FREE\","
                + "\"tags\":[\" Kafka \",\"Spring\",\"Kafka\",\"\"]"
                + "}";

        mockMvc.perform(post("/pc/course/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(321));

        ArgumentCaptor<CourseCreateRequest> requestCaptor = ArgumentCaptor.forClass(CourseCreateRequest.class);
        verify(oshCourseService).createCourse(requestCaptor.capture(), eq(currentUser));
        assertEquals(4, requestCaptor.getValue().getTags().size());
        assertEquals("FREE", requestCaptor.getValue().getResourceType());
        assertEquals(" Kafka ", requestCaptor.getValue().getTags().get(0));
        assertEquals("Spring", requestCaptor.getValue().getTags().get(1));
        assertEquals("Kafka", requestCaptor.getValue().getTags().get(2));
        assertEquals("", requestCaptor.getValue().getTags().get(3));
    }
}
