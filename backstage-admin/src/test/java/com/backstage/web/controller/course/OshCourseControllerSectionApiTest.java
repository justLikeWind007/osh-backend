package com.backstage.web.controller.course;

import com.backstage.RuoYiApplication;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.OshCourseQuestion;
import com.backstage.system.domain.course.OshCourseSection;
import com.backstage.system.domain.user.User;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseQuestionMapper;
import com.backstage.system.request.CourseChapterCreateRequest;
import com.backstage.system.request.CourseQuestionAnswerRequest;
import com.backstage.system.request.CourseSearchRequest;
import com.backstage.system.request.CourseSectionQuestionRequest;
import com.backstage.system.request.CourseVideoSectionCreateRequest;
import com.backstage.system.utils.UserContextUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RuoYiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class OshCourseControllerSectionApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OshCourseMapper oshCourseMapper;

    @Autowired
    private OshCourseQuestionMapper oshCourseQuestionMapper;

    @MockBean
    private UserContextUtil userContextUtil;

    @Test
    public void shouldInsertChapterSectionIntoDatabase() throws Exception {
        Long courseId = createTestCourse();
        when(userContextUtil.getCurrentUser()).thenReturn(buildCurrentUser());

        CourseChapterCreateRequest request = new CourseChapterCreateRequest();
        request.setCourseId(courseId);
        request.setTitle("集成测试一级章节");
        request.setSort(1);

        MvcResult mvcResult = mockMvc.perform(post("/pc/course/section/chapter/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        Long sectionId = extractDataId(mvcResult);
        OshCourseSection saved = oshCourseMapper.selectCourseSectionById(sectionId);
        assertNotNull(saved);
        assertEquals(courseId, saved.getCourseId());
        assertEquals(Long.valueOf(0L), saved.getParentId());
        assertEquals("集成测试一级章节", saved.getTitle());
        assertEquals(Integer.valueOf(1), saved.getFreeFlag());
        assertEquals(Integer.valueOf(1), saved.getStatus());
        assertEquals(Integer.valueOf(0), saved.getDeleteFlag());
        assertEquals("integration_test_user", saved.getCreateBy());
    }

    @Test
    public void shouldInsertVideoSectionWithTextContentIntoDatabase() throws Exception {
        Long courseId = createTestCourse();
        Long parentId = createParentChapter(courseId);
        when(userContextUtil.getCurrentUser()).thenReturn(buildCurrentUser());

        CourseVideoSectionCreateRequest request = new CourseVideoSectionCreateRequest();
        request.setCourseId(courseId);
        request.setParentId(parentId);
        request.setTitle("集成测试视频小节");
        request.setSort(2);
        request.setFreeFlag(0);
        request.setDuration(600);
        request.setMediaUrl("https://oss.example.com/integration-video.mp4");
        request.setCover("https://oss.example.com/video-cover.png");
        request.setVideoDesc("集成测试视频描述");
        request.setTextContent("这里是视频配套文本内容");
        request.setFileSize(654321L);

        MvcResult mvcResult = mockMvc.perform(post("/pc/course/section/video/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        Long sectionId = extractDataId(mvcResult);
        OshCourseSection saved = oshCourseMapper.selectCourseSectionById(sectionId);
        assertNotNull(saved);
        assertEquals(courseId, saved.getCourseId());
        assertEquals(parentId, saved.getParentId());
        assertEquals("video", saved.getType());
        assertEquals("https://oss.example.com/integration-video.mp4", saved.getMediaUrl());
        assertEquals("这里是视频配套文本内容", saved.getTextContent());
        assertEquals(Long.valueOf(654321L), saved.getFileSize());
        assertEquals("integration_test_user", saved.getCreateBy());
    }

    @Test
    public void shouldReturnNotFoundWhenSavingTextSection() throws Exception {
        Long courseId = createTestCourse();
        Long parentId = createParentChapter(courseId);
        when(userContextUtil.getCurrentUser()).thenReturn(buildCurrentUser());

        MvcResult mvcResult = mockMvc.perform(post("/pc/course/section/text/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseId\":" + courseId + ",\"parentId\":" + parentId + ",\"title\":\"集成测试图文小节\",\"sort\":3,\"freeFlag\":0,\"cover\":\"https://oss.example.com/text-cover.png\",\"textContent\":\"这里是集成测试图文内容\"}"))
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void shouldReturnNotFoundWhenGettingTextSection() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/pc/course/section/text/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(404, mvcResult.getResponse().getStatus());
    }

    @Test
    public void shouldSubmitQuestionForCourse935Section2WithUser1() throws Exception {
        when(userContextUtil.getCurrentUser()).thenReturn(buildUserOne());
        OshCourse beforeCourse = oshCourseMapper.selectCourseById(935L);
        int beforeQuestionCount = beforeCourse.getQuestionCount() == null ? 0 : beforeCourse.getQuestionCount();

        String suffix = String.valueOf(System.currentTimeMillis());
        CourseSectionQuestionRequest request = new CourseSectionQuestionRequest();
        request.setCourseId(935L);
        request.setSectionId(2L);
        request.setTitle("集成测试提问-" + suffix);
        request.setContent("这是用户1针对课程935章节2写入的测试提问-" + suffix);

        MvcResult mvcResult = mockMvc.perform(post("/pc/course/section/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        Long questionId = extractDataId(mvcResult);
        OshCourseQuestion saved = oshCourseQuestionMapper.selectQuestionById(questionId);
        assertNotNull(saved);
        assertEquals(Long.valueOf(935L), saved.getCourseId());
        assertEquals(Long.valueOf(2L), saved.getSectionId());
        assertEquals(Long.valueOf(1L), saved.getUserId());
        assertEquals(Integer.valueOf(1), saved.getRecordType());
        assertEquals("集成测试提问-" + suffix, saved.getTitle());
        assertEquals("这是用户1针对课程935章节2写入的测试提问-" + suffix, saved.getContent());
        OshCourse afterCourse = oshCourseMapper.selectCourseById(935L);
        assertEquals(beforeQuestionCount + 1, afterCourse.getQuestionCount().intValue());
    }

    @Test
    public void shouldSubmitAnswerForQuestionUnderCourse935Section2WithUser1() throws Exception {
        when(userContextUtil.getCurrentUser()).thenReturn(buildUserOne());

        String suffix = String.valueOf(System.currentTimeMillis());
        CourseSectionQuestionRequest questionRequest = new CourseSectionQuestionRequest();
        questionRequest.setCourseId(935L);
        questionRequest.setSectionId(2L);
        questionRequest.setTitle("集成测试回答前置提问-" + suffix);
        questionRequest.setContent("这是回答测试的前置提问-" + suffix);

        MvcResult questionResult = mockMvc.perform(post("/pc/course/section/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        Long questionId = extractDataId(questionResult);

        CourseQuestionAnswerRequest answerRequest = new CourseQuestionAnswerRequest();
        answerRequest.setQuestionId(questionId);
        answerRequest.setContent("这是用户1针对问题" + questionId + "写入的测试回答-" + suffix);

        MvcResult answerResult = mockMvc.perform(post("/pc/course/question/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        Long answerId = extractDataId(answerResult);
        OshCourseQuestion answer = oshCourseQuestionMapper.selectQuestionById(answerId);
        assertNotNull(answer);
        assertEquals(Long.valueOf(935L), answer.getCourseId());
        assertEquals(Long.valueOf(2L), answer.getSectionId());
        assertEquals(Long.valueOf(1L), answer.getUserId());
        assertEquals(Integer.valueOf(2), answer.getRecordType());
        assertEquals(questionId, answer.getQuestionId());
        assertEquals(questionId, answer.getParentId());
        assertEquals("这是用户1针对问题" + questionId + "写入的测试回答-" + suffix, answer.getContent());

        OshCourseQuestion question = oshCourseQuestionMapper.selectQuestionById(questionId);
        assertNotNull(question);
        assertEquals(Integer.valueOf(1), question.getReplyCount());
    }

    @Test
    public void shouldPageSectionQuestionsOrderedByCreateTimeDesc() throws Exception {
        when(userContextUtil.getCurrentUser()).thenReturn(buildUserOne());

        String suffix = String.valueOf(System.currentTimeMillis());

        CourseSectionQuestionRequest firstRequest = new CourseSectionQuestionRequest();
        firstRequest.setCourseId(935L);
        firstRequest.setSectionId(2L);
        firstRequest.setTitle("较早提问-" + suffix);
        firstRequest.setContent("较早提问内容-" + suffix);

        CourseSectionQuestionRequest secondRequest = new CourseSectionQuestionRequest();
        secondRequest.setCourseId(935L);
        secondRequest.setSectionId(2L);
        secondRequest.setTitle("较晚提问-" + suffix);
        secondRequest.setContent("较晚提问内容-" + suffix);

        mockMvc.perform(post("/pc/course/section/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Thread.sleep(5L);

        mockMvc.perform(post("/pc/course/section/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/pc/course/section/questions/935/2")
                        .param("pageNum", "1")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").exists())
                .andExpect(jsonPath("$.data.pageNum").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(2))
                .andExpect(jsonPath("$.data.rows[0].title").value("较晚提问-" + suffix))
                .andExpect(jsonPath("$.data.rows[1].title").value("较早提问-" + suffix));
    }

    @Test
    public void shouldListQuestionAnswersInCreateTimeOrder() throws Exception {
        when(userContextUtil.getCurrentUser()).thenReturn(buildUserOne());

        String suffix = String.valueOf(System.currentTimeMillis());
        CourseSectionQuestionRequest questionRequest = new CourseSectionQuestionRequest();
        questionRequest.setCourseId(935L);
        questionRequest.setSectionId(2L);
        questionRequest.setTitle("回答列表测试提问-" + suffix);
        questionRequest.setContent("回答列表测试提问内容-" + suffix);

        MvcResult questionResult = mockMvc.perform(post("/pc/course/section/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        Long questionId = extractDataId(questionResult);

        CourseQuestionAnswerRequest firstAnswerRequest = new CourseQuestionAnswerRequest();
        firstAnswerRequest.setQuestionId(questionId);
        firstAnswerRequest.setContent("第一条回答-" + suffix);

        CourseQuestionAnswerRequest secondAnswerRequest = new CourseQuestionAnswerRequest();
        secondAnswerRequest.setQuestionId(questionId);
        secondAnswerRequest.setContent("第二条回答-" + suffix);

        mockMvc.perform(post("/pc/course/question/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstAnswerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Thread.sleep(5L);

        mockMvc.perform(post("/pc/course/question/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondAnswerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/pc/course/question/answers/" + questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].content").value("第一条回答-" + suffix))
                .andExpect(jsonPath("$.data[1].content").value("第二条回答-" + suffix));
    }

    @Test
    public void shouldSearchCoursesWithCollectionAndBuyFlagsForLoggedInUser() throws Exception {
        when(userContextUtil.getCurrentUser()).thenReturn(buildUserOne());

        CourseSearchRequest request = new CourseSearchRequest();
        request.setKeyword("Spring Boot 企业级开发实战");
        request.setPageNum(1);
        request.setPageSize(10);

        mockMvc.perform(post("/pc/course/search/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rows[0].id").value(935))
                .andExpect(jsonPath("$.data.rows[0].buyFlag").value(1))
                .andExpect(jsonPath("$.data.rows[0].collectionFlag").value(0));
    }

    @Test
    public void shouldSearchCoursesOrderedBySalesCountThenCreateTimeDesc() throws Exception {
        CourseSearchRequest request = new CourseSearchRequest();
        request.setPageNum(1);
        request.setPageSize(2);

        mockMvc.perform(post("/pc/course/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rows[0].id").value(942))
                .andExpect(jsonPath("$.data.rows[1].id").value(957));
    }

    @Test
    public void shouldSearchLoginCoursesOrderedByBuyFlagThenScoreThenCreateTimeDesc() throws Exception {
        when(userContextUtil.getCurrentUser()).thenReturn(buildUserOne());

        CourseSearchRequest request = new CourseSearchRequest();
        request.setPageNum(1);
        request.setPageSize(2);

        mockMvc.perform(post("/pc/course/search/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rows[0].id").value(935))
                .andExpect(jsonPath("$.data.rows[1].id").value(942));
    }

    private Long extractDataId(MvcResult mvcResult) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        return jsonNode.get("data").asLong();
    }

    private Long createTestCourse() {
        String suffix = String.valueOf(System.currentTimeMillis());
        OshCourse course = new OshCourse();
        course.setTitle("集成测试课程-" + suffix);
        course.setCover("https://oss.example.com/course-cover.png");
        course.setIntro("集成测试课程简介");
        course.setServiceContent("集成测试服务内容");
        course.setPrice(new BigDecimal("0.01"));
        course.setTPrice(new BigDecimal("0.01"));
        course.setType("media");
        course.setStatus(0);
        course.setCreateBy("integration_test_user");
        course.setUpdateBy("integration_test_user");
        oshCourseMapper.insertCourse(course);
        return course.getId();
    }

    private Long createParentChapter(Long courseId) {
        OshCourseSection section = new OshCourseSection();
        section.setCourseId(courseId);
        section.setParentId(0L);
        section.setTitle("集成测试父章节");
        section.setSort(1);
        section.setFreeFlag(1);
        section.setStatus(1);
        section.setDeleteFlag(0);
        section.setCreateBy("integration_test_user");
        section.setUpdateBy("integration_test_user");
        oshCourseMapper.insertCourseSection(section);
        return section.getId();
    }

    private User buildCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("integration_test_user");
        return user;
    }

    private User buildUserOne() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user_1_test");
        return user;
    }
}
