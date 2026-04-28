package com.bachstage.course.job_stream_from_kafka_to_es.serialize;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CourseIndexJsonSerializerTest
{
    @Test
    public void shouldConvertVariablePrecisionTimeStringsToEpochMillisBeforeWritingEs()
    {
        JSONObject message = new JSONObject();
        message.put("id", 984L);
        message.put("createTime", "2026-04-26 23:49:50");
        message.put("updateTime", "2026-04-26 23:49:50.78");

        JSONObject serialized = JSON.parseObject(CourseIndexJsonSerializer.buildJsonWithTimestamp(message));

        assertEquals(Long.valueOf(1777218590000L), serialized.getLong("createTime"));
        assertEquals(Long.valueOf(1777218590780L), serialized.getLong("updateTime"));
    }
}
