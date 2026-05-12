package com.bachstage.tool.job_stream_from_kafka_to_es.kafka;

import com.bachstage.tool.job_stream_from_kafka_to_es.config.ToolIndexJobConfig;

import java.util.Properties;

/**
 * 工具索引 Kafka 消费参数工厂
 */
public class ToolKafkaPropertiesFactory
{
    public static Properties create(ToolIndexJobConfig config)
    {
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", config.getKafkaBootstrapServers());
        kafkaProps.setProperty("group.id", config.getKafkaGroupId());
        kafkaProps.setProperty("auto.offset.reset", config.getKafkaStartMode());
        kafkaProps.setProperty("enable.auto.commit", "false");
        kafkaProps.setProperty("auto.commit.interval.ms", "1000");
        kafkaProps.setProperty("session.timeout.ms", "30000");
        kafkaProps.setProperty("request.timeout.ms", "40000");
        return kafkaProps;
    }
}
