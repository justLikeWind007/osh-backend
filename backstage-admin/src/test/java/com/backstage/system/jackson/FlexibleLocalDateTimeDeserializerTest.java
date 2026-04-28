package com.backstage.system.jackson;

import com.backstage.system.domain.course.es.OshCourseEsDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FlexibleLocalDateTimeDeserializerTest
{
    @Test
    public void shouldParseBlankSeparatedDateTimeWithMillisecondsWhenConvertingEsDocument()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> source = new HashMap<>();
        source.put("id", 10001L);
        source.put("createTime", "2026-04-26 01:02:29.458");
        source.put("updateTime", "2026-04-26 02:03:30.123");

        OshCourseEsDocument document = objectMapper.convertValue(source, OshCourseEsDocument.class);

        assertEquals(LocalDateTime.of(2026, 4, 26, 1, 2, 29, 458_000_000), document.getCreateTime());
        assertEquals(LocalDateTime.of(2026, 4, 26, 2, 3, 30, 123_000_000), document.getUpdateTime());
    }
}
