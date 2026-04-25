package com.bachstage.course.job_stream_from_kafka_to_es;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bachstage.course.job_stream_from_kafka_to_es.config.CourseIndexJobConfig;
import com.bachstage.course.job_stream_from_kafka_to_es.es.CourseIndexElasticsearchSinkFactory;
import com.bachstage.course.job_stream_from_kafka_to_es.kafka.KafkaPropertiesFactory;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 课程搜索索引同步任务
 */
public class CourseSearchIndexSyncJob
{
    private static final Logger log = LoggerFactory.getLogger(CourseSearchIndexSyncJob.class);

    public static void main(String[] args) throws Exception
    {
        System.out.println("========================================");
        System.out.println("CourseSearchIndexSyncJob 启动中...");
        System.out.println("========================================");

        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        System.out.println("配置信息:");
        System.out.println("  Kafka: " + config.getKafkaBootstrapServers());
        System.out.println("  消费者组: " + config.getKafkaGroupId());
        System.out.println("  创建 Topic: " + config.getCreateTopic());
        System.out.println("  ES: " + config.getEsHosts());
        System.out.println("  ES 索引: " + config.getEsIndex());
        System.out.println("========================================\n");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(60000, CheckpointingMode.AT_LEAST_ONCE);

        Properties kafkaProps = KafkaPropertiesFactory.create(config);
        FlinkKafkaConsumer<String> createConsumer = new FlinkKafkaConsumer<>(
                config.getCreateTopic(), new SimpleStringSchema(), kafkaProps);
        createConsumer.setCommitOffsetsOnCheckpoints(true);

        DataStream<JSONObject> createStream = env
                .addSource(createConsumer)
                .name("course-index-create-source")
                .map((MapFunction<String, JSONObject>) value -> {
                    System.out.println("【创建流】收到消息: " + value);
                    return JSON.parseObject(value);
                })
                .name("course-index-create-parse")
                .filter(message -> {
                    Integer status = message.getInteger("status");
                    log.info("【创建流】过滤检查 - 课程ID: " + message.getLong("id")
                            + ", 状态: " + status);
                    boolean pass = status != null && status == 2;
                    System.out.println("【创建流】过滤检查 - 课程ID: " + message.getLong("id")
                            + ", 状态: " + status + ", 是否通过: " + pass);
                    return pass;
                })
                .name("course-index-create-filter");

        createStream.addSink(CourseIndexElasticsearchSinkFactory.buildCreateSink(config))
                .name("course-index-create-es-sink");

        System.out.println("Flink 任务已启动，仅监听新增课程消息...\n");
        env.execute("course-search-index-sync-job");
    }
}
