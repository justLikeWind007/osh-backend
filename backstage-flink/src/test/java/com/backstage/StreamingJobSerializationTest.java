package com.backstage;

import com.backstage.course.CourseIndexMessage;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class StreamingJobSerializationTest
{
    @Test
    public void shouldSerializeCreateSinkFunction() throws Exception
    {
        ElasticsearchSinkFunction<CourseIndexMessage> sinkFunction =
                StreamingJob.buildCreateSinkFunction("osh_course_index");

        serialize(sinkFunction);
    }

    @Test
    public void shouldSerializeUpdateSinkFunction() throws Exception
    {
        ElasticsearchSinkFunction<CourseIndexMessage> sinkFunction =
                StreamingJob.buildUpdateSinkFunction("osh_course_index");

        serialize(sinkFunction);
    }

    private void serialize(Object value) throws Exception
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(value);
        objectOutputStream.flush();
    }
}
