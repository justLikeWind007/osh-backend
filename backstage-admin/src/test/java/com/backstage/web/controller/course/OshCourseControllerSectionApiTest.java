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
import com.backstage.system.request.CourseTextSectionCreateRequest;
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
    public void shouldInsertTextSectionIntoDatabase() throws Exception {
        Long courseId = createTestCourse();
        Long parentId = createParentChapter(courseId);
        when(userContextUtil.getCurrentUser()).thenReturn(buildCurrentUser());

        CourseTextSectionCreateRequest request = new CourseTextSectionCreateRequest();
        request.setCourseId(courseId);
        request.setParentId(parentId);
        request.setTitle("集成测试图文小节");
        request.setSort(3);
        request.setFreeFlag(0);
        request.setCover("https://oss.example.com/text-cover.png");
        request.setTextContent("这里是集成测试图文内容");

        MvcResult mvcResult = mockMvc.perform(post("/pc/course/section/text/save")
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
        assertEquals("text", saved.getType());
        assertEquals("这里是集成测试图文内容", saved.getTextContent());
        assertEquals("integration_test_user", saved.getCreateBy());
    }

    @Test
    public void shouldSubmitQuestionForCourse935Section2WithUser1() throws Exception {
        when(userContextUtil.getCurrentUser()).thenReturn(buildUserOne());

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
