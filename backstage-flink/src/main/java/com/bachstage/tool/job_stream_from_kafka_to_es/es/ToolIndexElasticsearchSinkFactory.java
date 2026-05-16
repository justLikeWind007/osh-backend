package com.bachstage.tool.job_stream_from_kafka_to_es.es;

import com.alibaba.fastjson2.JSONObject;
import com.bachstage.course.job_stream_from_kafka_to_es.config.ApplicationPropertiesConfig;
import com.bachstage.course.job_stream_from_kafka_to_es.config.ElasticsearchAuthConfig;
import com.bachstage.course.job_stream_from_kafka_to_es.es.ElasticsearchHostParser;
import com.bachstage.tool.job_stream_from_kafka_to_es.config.ToolIndexJobConfig;
import com.bachstage.tool.job_stream_from_kafka_to_es.serialize.ToolIndexJsonSerializer;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具索引 Elasticsearch Sink 工厂
 */
public class ToolIndexElasticsearchSinkFactory
{
    public static ElasticsearchSink<JSONObject> buildUpsertSink(ToolIndexJobConfig config)
            throws MalformedURLException
    {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = ElasticsearchHostParser.parseHosts(config.getEsHosts());

        ElasticsearchSink.Builder<JSONObject> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<JSONObject>() {
                    @Override
                    public void process(JSONObject message, RuntimeContext context, RequestIndexer indexer) {
                        String jsonString = ToolIndexJsonSerializer.buildJsonWithTimestamp(message);
                        IndexRequest request = Requests.indexRequest()
                                .index(esIndex)
                                .type("_doc")
                                .id(String.valueOf(message.getLong("id")))
                                .source(jsonString, XContentType.JSON);
                        System.out.println("【ES upsert】工具ID: " + message.getLong("id")
                                + ", 事件: " + message.getString("eventType")
                                + ", 名称: " + message.getString("toolName"));
                        indexer.add(request);
                    }
                });
        configureBuilder(builder);
        return builder.build();
    }

    public static ElasticsearchSink<JSONObject> buildDeleteSink(ToolIndexJobConfig config)
            throws MalformedURLException
    {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = ElasticsearchHostParser.parseHosts(config.getEsHosts());

        ElasticsearchSink.Builder<JSONObject> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<JSONObject>() {
                    @Override
                    public void process(JSONObject message, RuntimeContext context, RequestIndexer indexer) {
                        Long toolId = message.getLong("id");
                        DeleteRequest request = new DeleteRequest(esIndex, "_doc", String.valueOf(toolId));
                        System.out.println("【ES delete】工具ID: " + toolId);
                        indexer.add(request);
                    }
                });
        configureBuilder(builder);
        return builder.build();
    }

    private static void configureBuilder(ElasticsearchSink.Builder<JSONObject> builder)
    {
        builder.setBulkFlushMaxActions(ApplicationPropertiesConfig.readInt(
                "elasticsearch.bulk-flush-max-actions", "ES_BULK_FLUSH_MAX_ACTIONS", 200));
        builder.setBulkFlushInterval(1000);
        builder.setFailureHandler((action, failure, restStatusCode, indexer) -> {
            System.err.println("【工具ES写入失败】status=" + restStatusCode
                    + ", action=" + action
                    + ", error=" + failure.getMessage());
            failure.printStackTrace();
            throw failure;
        });

        String auth = ElasticsearchAuthConfig.fromProperties().buildBasicAuth();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        Header[] headers = new Header[]{new BasicHeader("Authorization", "Basic " + encodedAuth)};
        builder.setRestClientFactory(restClientBuilder -> {
            restClientBuilder.setDefaultHeaders(headers);
            restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                    .setConnectTimeout(30000)
                    .setSocketTimeout(60000));
        });
    }

    /**
     * 审核状态 partial update sink。
     * 只更新 status / updateTime / updateBy 三个字段，不覆盖文档其他内容。
     * 由 ToolSearchIndexSyncJob 在识别到 AUDIT_APPROVED / AUDIT_REJECTED eventType 时调用。
     */
    public static ElasticsearchSink<JSONObject> buildAuditPartialUpdateSink(ToolIndexJobConfig config)
            throws MalformedURLException
    {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = ElasticsearchHostParser.parseHosts(config.getEsHosts());

        ElasticsearchSink.Builder<JSONObject> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new AuditPartialUpdateSinkFunction(esIndex));
        configureBuilder(builder);
        return builder.build();
    }

    private static final class AuditPartialUpdateSinkFunction
            implements ElasticsearchSinkFunction<JSONObject>
    {
        private static final long serialVersionUID = 1L;

        private static final java.time.format.DateTimeFormatter FORMATTER =
                new DateTimeFormatterBuilder()
                        .appendPattern("yyyy-MM-dd HH:mm:ss")
                        .optionalStart()
                        .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 3, true)
                        .optionalEnd()
                        .toFormatter();

        private final String esIndex;

        AuditPartialUpdateSinkFunction(String esIndex)
        {
            this.esIndex = esIndex;
        }

        @Override
        public void process(JSONObject message, RuntimeContext context, RequestIndexer indexer)
        {
            Long toolId = message.getLong("id");
            Map<String, Object> doc = new HashMap<>();
            Integer status = message.getInteger("status");
            if (status != null) {
                doc.put("status", status);
            }
            String updateBy = message.getString("updateBy");
            if (updateBy != null && !updateBy.trim().isEmpty()) {
                doc.put("updateBy", updateBy);
            }
            Object updateTimeRaw = message.get("updateTime");
            if (updateTimeRaw != null) {
                doc.put("updateTime", toEpochMillis(updateTimeRaw));
            }
            UpdateRequest request = new UpdateRequest(esIndex, "_doc", String.valueOf(toolId))
                    .doc(JSONObject.toJSONString(doc), XContentType.JSON)
                    .retryOnConflict(3);
            System.out.println("【ES audit partial update】工具ID: " + toolId
                    + ", status: " + status
                    + ", eventType: " + message.getString("eventType"));
            indexer.add(request);
        }

        private static long toEpochMillis(Object value)
        {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            String text = String.valueOf(value).trim();
            LocalDateTime ldt = LocalDateTime.parse(text, FORMATTER);
            return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }
}
