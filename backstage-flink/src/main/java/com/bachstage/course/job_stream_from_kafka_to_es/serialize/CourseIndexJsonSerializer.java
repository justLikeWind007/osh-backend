package com.bachstage.course.job_stream_from_kafka_to_es.serialize;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * 课程索引 JSON 序列化工具
 */
public class CourseIndexJsonSerializer
{
    public static String buildJsonWithTimestamp(JSONObject message)
    {
        JSONObject json = new JSONObject(message);
        return JSON.toJSONString(json);
    }
}
