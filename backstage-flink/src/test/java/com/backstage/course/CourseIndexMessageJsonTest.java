package com.backstage.course;

import com.alibaba.fastjson2.JSON;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CourseIndexMessageJsonTest
{
    @Test
    public void shouldParseCurrentCourseIndexPayload()
    {
        String payload = "{"
                + "\"id\":123,"
                + "\"title\":\"Kafka 课程\","
                + "\"resourceType\":\"FREE\","
                + "\"createBy\":\"tester\","
                + "\"updateBy\":\"tester\""
                + "}";

        CourseIndexMessage message = JSON.parseObject(payload, CourseIndexMessage.class);

        assertEquals(Long.valueOf(123L), message.getId());
        assertEquals(Long.valueOf(123L), message.getCourseId());
        assertEquals("FREE", message.getResourceType());
        assertEquals("tester", message.getCreateBy());
        assertEquals("tester", message.getUpdateBy());
    }

    @Test
    public void shouldSerializeEsCompatiblePayload()
    {
        CourseIndexMessage message = new CourseIndexMessage();
        message.setId(123L);
        message.setTitle("Kafka 课程");
        message.setResourceType("FREE");
        message.setCreateBy("tester");
        message.setUpdateBy("tester");
        message.setOperator("tester");

        String payload = JSON.toJSONString(message);

        assertTrue(payload.contains("\"id\":123"));
        assertTrue(payload.contains("\"resourceType\":\"FREE\""));
        assertTrue(payload.contains("\"createBy\":\"tester\""));
        assertTrue(payload.contains("\"updateBy\":\"tester\""));
        assertFalse(payload.contains("\"courseId\""));
        assertFalse(payload.contains("\"operator\""));
    }
}
