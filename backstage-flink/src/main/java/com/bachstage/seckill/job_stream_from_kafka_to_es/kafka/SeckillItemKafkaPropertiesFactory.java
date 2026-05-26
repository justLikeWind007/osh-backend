package com.bachstage.seckill.job_stream_from_kafka_to_es.kafka;

import com.bachstage.seckill.job_stream_from_kafka_to_es.config.SeckillItemJobConfig;

import java.util.Properties;

/**
 * 秒杀明细索引 Kafka 消费参数工厂
 */
public class SeckillItemKafkaPropertiesFactory {

    public static Properties create(SeckillItemJobConfig config) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", config.getKafkaBootstrapServers());
        props.setProperty("group.id", config.getKafkaGroupId());
        props.setProperty("auto.offset.reset", "latest");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("session.timeout.ms", "30000");
        props.setProperty("request.timeout.ms", "40000");
        return props;
    }
}
