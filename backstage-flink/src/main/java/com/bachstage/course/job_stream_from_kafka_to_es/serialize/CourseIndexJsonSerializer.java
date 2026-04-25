package com.bachstage.course.job_stream_from_kafka_to_es.serialize;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.Date;

/**
 * 课程索引 JSON 序列化工具
 */
public class CourseIndexJsonSerializer
{
    public static String buildJsonWithTimestamp(JSONObject message)
    {
        JSONObject json = new JSONObject(message);
        Object createTime = json.get("createTime");
        if (createTime instanceof Date)
        {
            json.put("createTime", ((Date) createTime).getTime());
        }
        Object updateTime = json.get("updateTime");
        if (updateTime instanceof Date)
        {
            json.put("updateTime", ((Date) updateTime).getTime());
        }
        return JSON.toJSONString(json);
    }
}
