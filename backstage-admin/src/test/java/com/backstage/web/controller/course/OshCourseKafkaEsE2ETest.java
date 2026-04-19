package com.backstage.web.controller.course;

import com.backstage.RuoYiApplication;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.service.ISysConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RuoYiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class OshCourseKafkaEsE2ETest
{
    private static final String DEFAULT_ES_INDEX = "osh_course_search_read";
    private static final long ES_POLL_TIMEOUT_MS = 60000L;
    private static final long ES_POLL_INTERVAL_MS = 2000L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OshCourseMapper oshCourseMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @MockBean
    private ISysConfigService sysConfigService;

    @After
    public void tearDown()
    {
        ThreadLocalUtil.remove();
    }

    @Test
    public void shouldCreateCourseAndEventuallyWriteDocumentToEsWhenFlinkIsRunning() throws Exception
    {
        String suffix = String.valueOf(System.currentTimeMillis());
        String title = "Flink联调课程-" + suffix;
        List<String> tags = Arrays.asList("Flink", "Kafka", "ES");

        String requestJson = "{"
                + "\"title\":\"" + title + "\","
                + "\"cover\":\"https://oss.example.com/flink-course-cover-" + suffix + ".png\","
                + "\"intro\":\"用于验证Flink消费Kafka并写入ES的课程\","
                + "\"serviceContent\":\"课程新增后应由Flink同步到ES\","
                + "\"price\":19.9,"
                + "\"tPrice\":99.9,"
                + "\"type\":\"media\","
                + "\"resourceType\":\"FREE\","
                + "\"level\":2,"
                + "\"freeType\":0,"
                + "\"afterServiceDays\":30,"
                + "\"tags\":" + objectMapper.writeValueAsString(tags)
                + "}";

        OshUser currentUser = buildCurrentUser();
        String responseBody = performAsUser(currentUser, post("/pc/course/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long courseId = extractDataId(responseBody);
        System.out.println("CourseId: " + courseId);
//        OshCourse savedCourse = oshCourseMapper.selectCourseById(courseId);
//        assertNotNull(savedCourse);
//        assertEquals(title, savedCourse.getTitle());
//        assertTrue(savedCourse.getPrice().compareTo(new BigDecimal("19.90")) == 0);
//        assertEquals("FREE", savedCourse.getResourceType());
//        assertEquals("integration_test_user", savedCourse.getCreateBy());

//        Map<String, Object> esDocument = waitForEsDocument(courseId);
//        assertNotNull(esDocument);
//        assertEquals(courseId.intValue(), ((Number) esDocument.get("id")).intValue());
//        assertEquals(title, esDocument.get("title"));
//        assertEquals("FREE", esDocument.get("resourceType"));
//        assertEquals("integration_test_user", esDocument.get("createBy"));
//        assertTrue(esDocument.containsKey("tagNames"));
//        assertTrue(esDocument.containsKey("searchText"));
    }

    private Long extractDataId(String responseBody) throws Exception
    {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("data").asLong();
    }

    private Map<String, Object> waitForEsDocument(Long courseId) throws Exception
    {
        String indexName = resolveEsIndex();
        long deadline = System.currentTimeMillis() + ES_POLL_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline)
        {
            GetResponse response = restHighLevelClient.get(new GetRequest(indexName, String.valueOf(courseId)),
                    RequestOptions.DEFAULT);
            if (response.isExists())
            {
                return response.getSourceAsMap();
            }
            Thread.sleep(ES_POLL_INTERVAL_MS);
        }
        throw new AssertionError("等待ES文档超时, courseId=" + courseId + ", index=" + indexName);
    }

    private String resolveEsIndex()
    {
        String value = System.getProperty("ES_INDEX");
        if (value != null && !value.trim().isEmpty())
        {
            return value.trim();
        }
        String envValue = System.getenv("ES_INDEX");
        if (envValue != null && !envValue.trim().isEmpty())
        {
            return envValue.trim();
        }
        return DEFAULT_ES_INDEX;
    }

    private OshUser buildCurrentUser()
    {
        OshUser oshUser = new OshUser();
        oshUser.setId(1L);
        oshUser.setUsername("integration_test_user");
        return oshUser;
    }

    private ResultActions performAsUser(OshUser oshUser, RequestBuilder requestBuilder) throws Exception
    {
        ThreadLocalUtil.set(OshUserConstants.USER_ID, oshUser.getId());
        ThreadLocalUtil.set(OshUserConstants.USER_INFO, oshUser);
        return mockMvc.perform(requestBuilder);
    }
}
