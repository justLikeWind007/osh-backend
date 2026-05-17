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
public class CourseSearchIndexSyncJob {
    private static final Logger log = LoggerFactory.getLogger(CourseSearchIndexSyncJob.class);
    private static final Integer PUBLISHED_STATUS = 2;

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("CourseSearchIndexSyncJob 启动中...");
        System.out.println("========================================");

        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        System.out.println("配置信息:");
        System.out.println("  Kafka: " + config.getKafkaBootstrapServers());
        System.out.println("  消费者组: " + config.getKafkaGroupId());
        System.out.println("  创建 Topic: " + config.getCreateTopic());
        System.out.println("  更新 Topic: " + config.getUpdateTopic());
        System.out.println("  删除 Topic: " + config.getDeleteTopic());
        System.out.println("  ES: " + config.getEsHosts());
        System.out.println("  ES 索引: " + config.getEsIndex());
        System.out.println("========================================\n");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(60000, CheckpointingMode.AT_LEAST_ONCE);

        Properties kafkaProps = KafkaPropertiesFactory.create(config);
        DataStream<JSONObject> createStream = buildCourseEventStream(
                env, kafkaProps, config.getCreateTopic(), "create", "创建流");
        DataStream<JSONObject> updateStream = buildCourseEventStream(
                env, kafkaProps, config.getUpdateTopic(), "update", "更新流");
        DataStream<JSONObject> deleteStream = buildDeleteEventStream(
                env, kafkaProps, config.getDeleteTopic(), "delete", "删除流");

        createStream.addSink(CourseIndexElasticsearchSinkFactory.buildUpsertSink(config))
                .name("course-index-create-es-upsert-sink");
        updateStream.addSink(CourseIndexElasticsearchSinkFactory.buildUpsertSink(config))
                .name("course-index-update-es-upsert-sink");
        deleteStream.addSink(CourseIndexElasticsearchSinkFactory.buildDeleteSink(config))
                .name("course-index-delete-es-delete-sink");

        System.out.println("Flink 任务已启动，正在同时监听课程创建、更新与删除消息...\n");
        env.execute("course-search-index-sync-job");
    }

    /**
     * 每种事件类型都保持一条独立链路，便于后续单独插入补数、转换、监控或失败处理逻辑。
     * 当前 create/update 的过滤规则一致，所以复用同一个构建方法，只通过 streamCode/streamLabel 区分日志与节点名。
     */
    private static DataStream<JSONObject> buildCourseEventStream(
            StreamExecutionEnvironment env,
            Properties kafkaProps,
            String topic,
            String streamCode,
            String streamLabel) {
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                topic, new SimpleStringSchema(), kafkaProps);
        consumer.setCommitOffsetsOnCheckpoints(true);

        return env
                .addSource(consumer)
                .name("course-index-" + streamCode + "-source")
                .map((MapFunction<String, JSONObject>) value -> {
                    System.out.println("【" + streamLabel + "】收到消息: " + value);
                    return parseJsonSafely(value, streamLabel);
                })
                .name("course-index-" + streamCode + "-parse")
                .filter(message -> message != null)
                .name("course-index-" + streamCode + "-valid-json-filter")
                .filter(message -> {
                    Integer status = message.getInteger("status");
                    boolean pass = status != null && PUBLISHED_STATUS.equals(status);
                    log.info("【{}】过滤检查 - 课程ID: {}, 状态: {}, 是否通过: {}",
                            streamLabel, message.getLong("id"), status, pass);
                    return pass;
                })
                .name("course-index-" + streamCode + "-filter");
    }

    /**
     * 删除流只负责把“删除哪门课程”这件事传递到 ES。
     * 这里不再关心课程状态，只要求消息里必须包含课程 ID；具体删除语义由业务端发送 delete topic 来表达。
     */
    private static DataStream<JSONObject> buildDeleteEventStream(StreamExecutionEnvironment env,
                                                                 Properties kafkaProps,
                                                                 String topic,
                                                                 String streamCode,
                                                                 String streamLabel)
    {
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                topic, new SimpleStringSchema(), kafkaProps);
        consumer.setCommitOffsetsOnCheckpoints(true);

        return env
                .addSource(consumer)
                .name("course-index-" + streamCode + "-source")
                .map((MapFunction<String, JSONObject>) value -> {
                    System.out.println("【" + streamLabel + "】收到消息: " + value);
                    return parseJsonSafely(value, streamLabel);
                })
                .name("course-index-" + streamCode + "-parse")
                .filter(message -> message != null)
                .name("course-index-" + streamCode + "-valid-json-filter")
                .filter(message -> {
                    Long courseId = message.getLong("id");
                    boolean pass = courseId != null;
                    log.info("【{}】过滤检查 - 课程ID: {}, 是否通过: {}",
                            streamLabel, courseId, pass);
                    return pass;
                })
                .name("course-index-" + streamCode + "-filter");
    }

    private static JSONObject parseJsonSafely(String value, String streamLabel) {
        try {
            return JSON.parseObject(value);
        } catch (Exception ex) {
            log.warn("【{}】跳过非法JSON消息: {}", streamLabel, value, ex);
            return null;
        }
    }
}
