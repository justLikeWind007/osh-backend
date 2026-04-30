package com.bachstage.course.job_stream_from_kafka_to_es.config;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CourseIndexJobConfigTest
{
    @After
    public void tearDown()
    {
        System.clearProperty("course.index.update-topic");
        System.clearProperty("course.index.delete-topic");
    }

    @Test
    public void shouldUseDefaultUpdateTopicWhenSystemPropertyIsMissing()
    {
        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        assertEquals("osh.course.index.update", config.getUpdateTopic());
    }

    @Test
    public void shouldPreferSystemPropertyUpdateTopicWhenProvided()
    {
        System.setProperty("course.index.update-topic", "osh.course.index.update.test");

        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        assertEquals("osh.course.index.update.test", config.getUpdateTopic());
    }

    @Test
    public void shouldUseDefaultDeleteTopicWhenSystemPropertyIsMissing()
    {
        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        assertEquals("osh.course.index.delete", config.getDeleteTopic());
    }

    @Test
    public void shouldPreferSystemPropertyDeleteTopicWhenProvided()
    {
        System.setProperty("course.index.delete-topic", "osh.course.index.delete.test");

        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        assertEquals("osh.course.index.delete.test", config.getDeleteTopic());
    }
}
