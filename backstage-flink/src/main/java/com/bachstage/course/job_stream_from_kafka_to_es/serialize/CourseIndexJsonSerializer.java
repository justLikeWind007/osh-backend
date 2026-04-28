package com.bachstage.course.job_stream_from_kafka_to_es.serialize;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * 课程索引 JSON 序列化工具
 */
public class CourseIndexJsonSerializer
{
    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_TIME = "updateTime";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 3, true)
            .optionalEnd()
            .toFormatter();

    public static String buildJsonWithTimestamp(JSONObject message)
    {
        JSONObject json = new JSONObject(message);
        normalizeDateTimeField(json, CREATE_TIME);
        normalizeDateTimeField(json, UPDATE_TIME);
        return JSON.toJSONString(json);
    }

    private static void normalizeDateTimeField(JSONObject json, String fieldName)
    {
        Object value = json.get(fieldName);
        if (!(value instanceof String))
        {
            return;
        }

        String text = ((String) value).trim();
        if (text.isEmpty())
        {
            json.remove(fieldName);
            return;
        }

        LocalDateTime localDateTime = LocalDateTime.parse(text, DATE_TIME_FORMATTER);
        long epochMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        json.put(fieldName, epochMillis);
    }
}
