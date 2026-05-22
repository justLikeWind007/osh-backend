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
        System.clearProperty("flink.parallelism");
        System.clearProperty("flink.checkpoint.interval-ms");
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

    @Test
    public void shouldUseDefaultFlinkRuntimeSettingsWhenSystemPropertyIsMissing()
    {
        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        assertEquals(1, config.getParallelism());
        assertEquals(60000L, config.getCheckpointIntervalMs());
    }

    @Test
    public void shouldPreferSystemPropertyFlinkRuntimeSettingsWhenProvided()
    {
        System.setProperty("flink.parallelism", "2");
        System.setProperty("flink.checkpoint.interval-ms", "120000");

        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        assertEquals(2, config.getParallelism());
        assertEquals(120000L, config.getCheckpointIntervalMs());
    }
}
