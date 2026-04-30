package com.bachstage.course.job_stream_from_kafka_to_es.kafka;

import com.bachstage.course.job_stream_from_kafka_to_es.config.CourseIndexJobConfig;

import java.util.Properties;

/**
 * Kafka 消费参数工厂
 */
public class KafkaPropertiesFactory
{
    public static Properties create(CourseIndexJobConfig config)
    {
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", config.getKafkaBootstrapServers());
        kafkaProps.setProperty("group.id", config.getKafkaGroupId());
        kafkaProps.setProperty("auto.offset.reset", "latest");
        kafkaProps.setProperty("enable.auto.commit", "false");
        kafkaProps.setProperty("auto.commit.interval.ms", "1000");
        kafkaProps.setProperty("session.timeout.ms", "30000");
        kafkaProps.setProperty("request.timeout.ms", "40000");
        return kafkaProps;
    }
}
