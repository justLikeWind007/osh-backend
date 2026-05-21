package com.bachstage.tool.job_stream_from_kafka_to_es;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bachstage.tool.job_stream_from_kafka_to_es.config.ToolIndexJobConfig;
import com.bachstage.tool.job_stream_from_kafka_to_es.es.ToolIndexElasticsearchSinkFactory;
import com.bachstage.tool.job_stream_from_kafka_to_es.kafka.ToolKafkaPropertiesFactory;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 工具搜索索引同步任务
 */
public class ToolSearchIndexSyncJob
{
    private static final Logger log = LoggerFactory.getLogger(ToolSearchIndexSyncJob.class);
    private static final String EVENT_TYPE_CREATE = "TOOL_INDEX_CREATE";
    private static final String EVENT_TYPE_UPDATE = "TOOL_INDEX_UPDATE";
    private static final String EVENT_TYPE_DELETE = "TOOL_INDEX_DELETE";
    private static final String EVENT_TYPE_COUNTER = "TOOL_INDEX_COUNTER";
    private static final String EVENT_TYPE_AUDIT_APPROVED = "AUDIT_APPROVED";
    private static final String EVENT_TYPE_AUDIT_REJECTED = "AUDIT_REJECTED";
    private static final Integer PUBLISHED_STATUS = 4;

    public static void main(String[] args) throws Exception
    {
        log.info("========================================");
        log.info("ToolSearchIndexSyncJob 启动中...");
        log.info("========================================");

        ToolIndexJobConfig config = ToolIndexJobConfig.fromSystem();

        log.info("配置信息: Kafka={}, 消费者组={}, Offset策略={}, Topic={}, ES={}, ES索引={}",
                config.getKafkaBootstrapServers(), config.getKafkaGroupId(), config.getKafkaStartMode(),
                config.getTopic(), config.getEsHosts(), config.getEsIndex());

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(config.getParallelism());
        env.enableCheckpointing(config.getCheckpointIntervalMs(), CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(config.getCheckpointMinPauseMs());
        env.getCheckpointConfig().setCheckpointTimeout(config.getCheckpointTimeoutMs());
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(config.getMaxConcurrentCheckpoints());
        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(config.getTolerableCheckpointFailureNumber());
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                config.getRestartAttempts(),
                org.apache.flink.api.common.time.Time.milliseconds(config.getRestartDelayMs())));

        Properties kafkaProps = ToolKafkaPropertiesFactory.create(config);
        DataStream<JSONObject> eventStream = buildToolEventStream(env, kafkaProps, config.getTopic());

        // 审核状态变更：只做 partial update（status / updateTime / updateBy）
        eventStream
                .filter(ToolSearchIndexSyncJob::isAuditEvent)
                .name("tool-index-audit-event-filter")
                .addSink(ToolIndexElasticsearchSinkFactory.buildAuditPartialUpdateSink(config))
                .name("tool-index-es-audit-partial-update-sink");

        // 常规 upsert：已发布才写入
        eventStream
                .filter(ToolSearchIndexSyncJob::isUpsertEvent)
                .name("tool-index-upsert-event-filter")
                .filter(ToolSearchIndexSyncJob::isPublished)
                .name("tool-index-published-filter")
                .addSink(ToolIndexElasticsearchSinkFactory.buildUpsertSink(config))
                .name("tool-index-es-upsert-sink");

        // 常规删除：DELETE 事件或未发布的 upsert
        eventStream
                .filter(message -> isDeleteEvent(message) || isNotPublishedUpsertEvent(message))
                .name("tool-index-es-delete-route-filter")
                .addSink(ToolIndexElasticsearchSinkFactory.buildDeleteSink(config))
                .name("tool-index-es-delete-sink");

        log.info("Flink 任务已启动，正在监听工具索引统一 Topic...");
        env.execute("tool-search-index-sync-job");
    }

    private static DataStream<JSONObject> buildToolEventStream(StreamExecutionEnvironment env,
                                                               Properties kafkaProps,
                                                               String topic)
    {
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                topic, new SimpleStringSchema(), kafkaProps);
        consumer.setCommitOffsetsOnCheckpoints(true);

        return env
                .addSource(consumer)
                .name("tool-index-source")
                .map((MapFunction<String, JSONObject>) value -> {
                    log.info("【工具索引】收到消息: {}", value);
                    try {
                        return JSON.parseObject(value);
                    } catch (Exception ex) {
                        log.error("【工具索引】解析消息失败，跳过该条非JSON数据: {}", value, ex);
                        return null;
                    }
                })
                .name("tool-index-parse")
                .filter(message -> message != null)
                .name("tool-index-json-filter")
                .filter(message -> {
                    Long toolId = message.getLong("id");
                    String eventType = message.getString("eventType");
                    boolean pass = toolId != null && eventType != null && !eventType.trim().isEmpty();
                    log.info("【工具索引】基础过滤 - 工具ID: {}, eventType: {}, 是否通过: {}", toolId, eventType, pass);
                    return pass;
                })
                .name("tool-index-basic-filter");
    }

    private static boolean isUpsertEvent(JSONObject message)
    {
        String eventType = message.getString("eventType");
        return EVENT_TYPE_CREATE.equals(eventType)
                || EVENT_TYPE_UPDATE.equals(eventType)
                || EVENT_TYPE_COUNTER.equals(eventType);
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
        log.info("【工具索引】上架过滤 - 工具ID: {}, 状态: {}, 是否通过: {}",
                message.getLong("id"), status, pass);
        return pass;
    }
}
