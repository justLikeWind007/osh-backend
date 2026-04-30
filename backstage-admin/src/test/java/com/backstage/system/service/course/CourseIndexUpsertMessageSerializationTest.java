package com.backstage.system.service.course;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class CourseIndexUpsertMessageSerializationTest
{
    @Test
    public void shouldSerializeCourseIndexMessageTimeWithFixedThreeDigitMilliseconds()
    {
        CourseIndexUpsertMessage message = new CourseIndexUpsertMessage();
        message.setId(10001L);
        message.setCreateTime(new Date(1745683200780L));
        message.setUpdateTime(new Date(1745683207005L));

        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(message));

        assertEquals("2025-04-27 00:00:00.780", jsonObject.getString("createTime"));
        assertEquals("2025-04-27 00:00:07.005", jsonObject.getString("updateTime"));
    }
}
