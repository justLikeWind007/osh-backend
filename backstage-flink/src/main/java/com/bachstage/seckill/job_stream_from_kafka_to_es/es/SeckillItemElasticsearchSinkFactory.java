package com.bachstage.seckill.job_stream_from_kafka_to_es.es;

import com.alibaba.fastjson2.JSONObject;
import com.bachstage.course.job_stream_from_kafka_to_es.config.ApplicationPropertiesConfig;
import com.bachstage.course.job_stream_from_kafka_to_es.config.ElasticsearchAuthConfig;
import com.bachstage.course.job_stream_from_kafka_to_es.es.ElasticsearchHostParser;
import com.bachstage.seckill.job_stream_from_kafka_to_es.config.SeckillItemJobConfig;
import com.bachstage.seckill.job_stream_from_kafka_to_es.serialize.SeckillItemJsonSerializer;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.Base64;
import java.util.List;

/**
 * 秒杀商品明细索引 Elasticsearch Sink 工厂
 */
public class SeckillItemElasticsearchSinkFactory {

    private static final Logger log = LoggerFactory.getLogger(SeckillItemElasticsearchSinkFactory.class);

    /**
     * 构建 upsert sink：将明细文档写入 ES（IndexRequest，覆盖写）
     */
    public static ElasticsearchSink<JSONObject> buildUpsertSink(SeckillItemJobConfig config)
            throws MalformedURLException {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = ElasticsearchHostParser.parseHosts(config.getEsHosts());

        ElasticsearchSink.Builder<JSONObject> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<JSONObject>() {
                    @Override
                    public void process(JSONObject message, RuntimeContext ctx, RequestIndexer indexer) {
                        String jsonString = SeckillItemJsonSerializer.buildJsonWithTimestamp(message);
                        IndexRequest request = Requests.indexRequest()
                                .index(esIndex)
                                .type("_doc")
                                .id(String.valueOf(message.getLong("id")))
                                .source(jsonString, XContentType.JSON);
                        log.info("【秒杀ES upsert】明细ID: {}, 标题: {}",
                                message.getLong("id"), message.getString("title"));
                        indexer.add(request);
                    }
                });
        configureBuilder(builder, "秒杀明细upsert");
        return builder.build();
    }

    /**
     * 构建 delete sink：从 ES 删除明细文档（DeleteRequest）
     */
    public static ElasticsearchSink<JSONObject> buildDeleteSink(SeckillItemJobConfig config)
            throws MalformedURLException {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = ElasticsearchHostParser.parseHosts(config.getEsHosts());

        ElasticsearchSink.Builder<JSONObject> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<JSONObject>() {
                    @Override
                    public void process(JSONObject message, RuntimeContext ctx, RequestIndexer indexer) {
                        Long itemId = message.getLong("id");
                        DeleteRequest request = new DeleteRequest(esIndex, "_doc", String.valueOf(itemId));
                        log.info("【秒杀ES delete】明细ID: {}", itemId);
                        indexer.add(request);
                    }
                });
        configureBuilder(builder, "秒杀明细delete");
        return builder.build();
    }

    private static void configureBuilder(ElasticsearchSink.Builder<JSONObject> builder, String label) {
        builder.setBulkFlushMaxActions(ApplicationPropertiesConfig.readInt(
                "elasticsearch.bulk-flush-max-actions", "ES_BULK_FLUSH_MAX_ACTIONS", 200));
        builder.setBulkFlushInterval(1000);
        builder.setFailureHandler((action, failure, restStatusCode, indexer) -> {
            log.error("【秒杀ES写入失败】sink={}, status={}, action={}, error={}",
                    label, restStatusCode, action, failure.getMessage(), failure);
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
}
