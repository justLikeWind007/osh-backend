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
    private static final String EVENT_TYPE_CREATE = "COURSE_INDEX_CREATE";
    private static final String EVENT_TYPE_UPDATE = "COURSE_INDEX_UPDATE";
    private static final String EVENT_TYPE_DELETE = "COURSE_INDEX_DELETE";
    private static final String EVENT_TYPE_AUDIT_APPROVED = "AUDIT_APPROVED";
    private static final String EVENT_TYPE_AUDIT_REJECTED = "AUDIT_REJECTED";
    private static final Integer PUBLISHED_STATUS = 4;

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("CourseSearchIndexSyncJob 启动中...");
        System.out.println("========================================");

        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();

        System.out.println("配置信息:");
        System.out.println("  Kafka: " + config.getKafkaBootstrapServers());
        System.out.println("  消费者组: " + config.getKafkaGroupId());
        System.out.println("  Topic: " + config.getTopic());
        System.out.println("  ES: " + config.getEsHosts());
        System.out.println("  ES 索引: " + config.getEsIndex());
        System.out.println("========================================\n");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(60000, CheckpointingMode.AT_LEAST_ONCE);

        Properties kafkaProps = KafkaPropertiesFactory.create(config);
        DataStream<JSONObject> eventStream = buildCourseEventStream(
                env, kafkaProps, config.getTopic(), "course", "课程索引流");

        // 审核状态变更：只做 partial update（status / updateTime / updateBy）
        eventStream
                .filter(CourseSearchIndexSyncJob::isAuditEvent)
                .name("course-index-audit-event-filter")
                .addSink(CourseIndexElasticsearchSinkFactory.buildAuditPartialUpdateSink(config))
                .name("course-index-es-audit-partial-update-sink");

        // 常规 upsert：已发布才写入
        eventStream
                .filter(CourseSearchIndexSyncJob::isUpsertEvent)
                .name("course-index-upsert-event-filter")
                .filter(CourseSearchIndexSyncJob::isPublished)
                .name("course-index-published-filter")
                .addSink(CourseIndexElasticsearchSinkFactory.buildUpsertSink(config))
                .name("course-index-es-upsert-sink");

        // 常规删除：DELETE 事件或未发布的 upsert
        eventStream
                .filter(message -> isDeleteEvent(message) || isNotPublishedUpsertEvent(message))
                .name("course-index-es-delete-route-filter")
                .addSink(CourseIndexElasticsearchSinkFactory.buildDeleteSink(config))
                .name("course-index-es-delete-sink");

        System.out.println("Flink 任务已启动，正在监听课程索引统一 Topic...\n");
        env.execute("course-search-index-sync-job");
    }

    /**
     * 课程索引统一消费单 topic，通过 eventType 分流。
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
                    try {
                        return JSON.parseObject(value);
                    } catch (Exception ex) {
                        log.error("【{}】解析消息失败，跳过该条非JSON数据: {}", streamLabel, value, ex);
                        return null;
                    }
                })
                .name("course-index-" + streamCode + "-parse")
                .filter(message -> message != null)
                .name("course-index-" + streamCode + "-json-filter")
                .filter(message -> {
                    Long courseId = message.getLong("id");
                    String eventType = message.getString("eventType");
                    boolean pass = courseId != null && eventType != null && !eventType.trim().isEmpty();
                    log.info("【{}】基础过滤 - 课程ID: {}, eventType: {}, 是否通过: {}",
                            streamLabel, courseId, eventType, pass);
                    return pass;
                })
                .name("course-index-" + streamCode + "-filter");
    }

    private static boolean isUpsertEvent(JSONObject message)
    {
        String eventType = message.getString("eventType");
        return EVENT_TYPE_CREATE.equals(eventType) || EVENT_TYPE_UPDATE.equals(eventType);
    }

    private static boolean isAuditEvent(JSONObject message)
    {
        String eventType = message.getString("eventType");
        return EVENT_TYPE_AUDIT_APPROVED.equals(eventType) || EVENT_TYPE_AUDIT_REJECTED.equals(eventType);
    }

    private static boolean isDeleteEvent(JSONObject message)
    {
        return EVENT_TYPE_DELETE.equals(message.getString("eventType"));
    }

    private static boolean isNotPublishedUpsertEvent(JSONObject message)
    {
        return isUpsertEvent(message) && !isPublished(message);
    }

    private static boolean isPublished(JSONObject message)
    {
        Integer status = message.getInteger("status");
        boolean pass = status != null && PUBLISHED_STATUS.equals(status);
        log.info("【课程索引】上架过滤 - 课程ID: {}, 状态: {}, 是否通过: {}",
                message.getLong("id"), status, pass);
        return pass;
    }
}
