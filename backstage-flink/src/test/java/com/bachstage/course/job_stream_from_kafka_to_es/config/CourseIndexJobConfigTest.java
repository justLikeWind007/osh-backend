package com.bachstage.course.job_stream_from_kafka_to_es.config;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CourseIndexJobConfigTest
{
    @After
    public void tearDown()
    {
        System.clearProperty("course.index.topic");
    }

    @Test
    public void shouldUseDefaultTopicWhenSystemPropertyIsMissing()
    {
        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        assertEquals("osh.course.index", config.getTopic());
    }

    @Test
    public void shouldPreferSystemPropertyTopicWhenProvided()
    {
        System.setProperty("course.index.topic", "osh.course.index.test");

        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        assertEquals("osh.course.index.test", config.getTopic());
    }
}
