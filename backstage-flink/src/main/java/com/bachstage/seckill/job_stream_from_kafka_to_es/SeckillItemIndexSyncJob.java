package com.bachstage.seckill.job_stream_from_kafka_to_es;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bachstage.seckill.job_stream_from_kafka_to_es.config.SeckillItemJobConfig;
import com.bachstage.seckill.job_stream_from_kafka_to_es.es.SeckillItemElasticsearchSinkFactory;
import com.bachstage.seckill.job_stream_from_kafka_to_es.kafka.SeckillItemKafkaPropertiesFactory;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 秒杀商品明细搜索索引同步任务
 *
 * 消费 Kafka Topic: osh.seckill.item.index
 * 按 eventType 分流：
 *   - SECKILL_ITEM_INDEX_CREATE/UPDATE + activityStatus=2（进行中）→ upsert 到 ES
 *   - SECKILL_ITEM_INDEX_CREATE/UPDATE + activityStatus≠2（非进行中）→ 从 ES 删除
 *   - SECKILL_ITEM_INDEX_DELETE → 从 ES 删除
 */
public class SeckillItemIndexSyncJob {

    private static final Logger log = LoggerFactory.getLogger(SeckillItemIndexSyncJob.class);

    private static final String EVENT_TYPE_CREATE = "SECKILL_ITEM_INDEX_CREATE";
    private static final String EVENT_TYPE_UPDATE = "SECKILL_ITEM_INDEX_UPDATE";
    private static final String EVENT_TYPE_DELETE = "SECKILL_ITEM_INDEX_DELETE";

    /** activityStatus=2 表示进行中，只有进行中的活动商品才写入 ES */
    private static final int ACTIVITY_STATUS_ONGOING = 2;

    public static void main(String[] args) throws Exception {
        log.info("========================================");
        log.info("SeckillItemIndexSyncJob 启动中...");
        log.info("========================================");

        SeckillItemJobConfig config = SeckillItemJobConfig.fromSystem();

        log.info("配置信息: Kafka={}, 消费者组={}, Topic={}, ES={}, ES索引={}",
                config.getKafkaBootstrapServers(), config.getKafkaGroupId(),
                config.getTopic(), config.getEsHosts(), config.getEsIndex());

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(60000, CheckpointingMode.AT_LEAST_ONCE);

        Properties kafkaProps = SeckillItemKafkaPropertiesFactory.create(config);

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                config.getTopic(), new SimpleStringSchema(), kafkaProps);
        consumer.setCommitOffsetsOnCheckpoints(true);

        // 解析 Kafka 消息为 JSONObject
        DataStream<JSONObject> eventStream = env
                .addSource(consumer)
                .name("seckill-item-index-source")
                .map((MapFunction<String, JSONObject>) value -> {
                    log.info("【秒杀明细索引】收到消息: {}", value);
                    try {
                        return JSON.parseObject(value);
                    } catch (Exception ex) {
                        log.error("【秒杀明细索引】解析消息失败，跳过: {}", value, ex);
                        return null;
                    }
                })
                .name("seckill-item-index-parse")
                .filter(msg -> msg != null)
                .name("seckill-item-index-null-filter")
                .filter(msg -> {
                    Long itemId = msg.getLong("id");
                    String eventType = msg.getString("eventType");
                    boolean pass = itemId != null && eventType != null && !eventType.trim().isEmpty();
                    if (!pass) {
                        log.warn("【秒杀明细索引】消息缺少 id 或 eventType，跳过: {}", msg);
                    }
                    return pass;
                })
                .name("seckill-item-index-basic-filter");

        // upsert 流：CREATE/UPDATE 且 activityStatus=2（进行中）
        eventStream
                .filter(SeckillItemIndexSyncJob::isUpsertEvent)
                .name("seckill-item-upsert-event-filter")
                .filter(SeckillItemIndexSyncJob::isOngoing)
                .name("seckill-item-ongoing-filter")
                .addSink(SeckillItemElasticsearchSinkFactory.buildUpsertSink(config))
                .name("seckill-item-es-upsert-sink");

        // delete 流：DELETE 事件，或 CREATE/UPDATE 但 activityStatus≠2（非进行中，从 ES 移除）
        eventStream
                .filter(msg -> isDeleteEvent(msg) || isNotOngoingUpsertEvent(msg))
                .name("seckill-item-delete-route-filter")
                .addSink(SeckillItemElasticsearchSinkFactory.buildDeleteSink(config))
                .name("seckill-item-es-delete-sink");

        log.info("SeckillItemIndexSyncJob 已启动，正在监听 Topic: {}", config.getTopic());
        env.execute("seckill-item-index-sync-job");
    }

    private static boolean isUpsertEvent(JSONObject msg) {
        String eventType = msg.getString("eventType");
        return EVENT_TYPE_CREATE.equals(eventType) || EVENT_TYPE_UPDATE.equals(eventType);
    }

    private static boolean isDeleteEvent(JSONObject msg) {
        return EVENT_TYPE_DELETE.equals(msg.getString("eventType"));
    }

    private static boolean isOngoing(JSONObject msg) {
        Integer activityStatus = msg.getInteger("activityStatus");
        boolean pass = activityStatus != null && ACTIVITY_STATUS_ONGOING == activityStatus;
        log.info("【秒杀明细索引】进行中过滤 - 明细ID: {}, activityStatus: {}, 是否通过: {}",
                msg.getLong("id"), activityStatus, pass);
        return pass;
    }

    private static boolean isNotOngoingUpsertEvent(JSONObject msg) {
        return isUpsertEvent(msg) && !isOngoing(msg);
    }
}
