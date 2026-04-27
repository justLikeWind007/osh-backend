package com.bachstage.course.job_stream_from_kafka_to_es.es;

import com.alibaba.fastjson2.JSONObject;
import com.bachstage.course.job_stream_from_kafka_to_es.config.ApplicationPropertiesConfig;
import com.bachstage.course.job_stream_from_kafka_to_es.config.CourseIndexJobConfig;
import com.bachstage.course.job_stream_from_kafka_to_es.config.ElasticsearchAuthConfig;
import com.bachstage.course.job_stream_from_kafka_to_es.serialize.CourseIndexJsonSerializer;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;

import java.net.MalformedURLException;
import java.util.Base64;
import java.util.List;

/**
 * 课程索引 Elasticsearch Sink 工厂
 */
public class CourseIndexElasticsearchSinkFactory
{
    /**
     * create/update 都写入同一个课程文档 ID。
     * ES 侧看到的是同一份课程索引文档被不断覆盖更新，因此这里统一按 upsert 语义构建 sink。
     */
    public static ElasticsearchSink<JSONObject> buildUpsertSink(CourseIndexJobConfig config)
            throws MalformedURLException
    {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = ElasticsearchHostParser.parseHosts(config.getEsHosts());

        ElasticsearchSink.Builder<JSONObject> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<JSONObject>() {
                    @Override
                    public void process(JSONObject message, RuntimeContext context, RequestIndexer indexer) {
                        String jsonString = CourseIndexJsonSerializer.buildJsonWithTimestamp(message);
                        IndexRequest request = Requests.indexRequest()
                                .index(esIndex)
                                .type("_doc")
                                .id(String.valueOf(message.getLong("id")))
                                .source(jsonString, XContentType.JSON);
                        System.out.println("【ES upsert】课程ID: " + message.getLong("id")
                                + ", 标题: " + message.getString("title"));
                        indexer.add(request);
                    }
                });
        builder.setFailureHandler((action, failure, restStatusCode, indexer) -> {
            System.err.println("【ES写入失败】status=" + restStatusCode
                    + ", action=" + action
                    + ", error=" + failure.getMessage());
            failure.printStackTrace();
        });


        builder.setBulkFlushMaxActions(ApplicationPropertiesConfig.readInt(
                "elasticsearch.bulk-flush-max-actions", "ES_BULK_FLUSH_MAX_ACTIONS", 200));
        builder.setBulkFlushInterval(1000);
        builder.setFailureHandler((action, failure, restStatusCode, indexer) -> {
            System.err.println("【ES写入失败】status=" + restStatusCode
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

        return builder.build();
    }

    /**
     * 删除消息和 upsert 消息的语义不同。
     * 这里单独使用 delete request，避免把“删除”伪装成一次普通更新，后续排查链路时也更直观。
     */
    public static ElasticsearchSink<JSONObject> buildDeleteSink(CourseIndexJobConfig config)
            throws MalformedURLException
    {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = ElasticsearchHostParser.parseHosts(config.getEsHosts());

        ElasticsearchSink.Builder<JSONObject> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<JSONObject>() {
                    @Override
                    public void process(JSONObject message, RuntimeContext context, RequestIndexer indexer) {
                        Long courseId = message.getLong("id");
                        DeleteRequest request = new DeleteRequest(esIndex, "_doc", String.valueOf(courseId));
                        System.out.println("【ES delete】课程ID: " + courseId);
                        indexer.add(request);
                    }
                });
        builder.setFailureHandler((action, failure, restStatusCode, indexer) -> {
            System.err.println("【ES删除失败】status=" + restStatusCode
                    + ", action=" + action
                    + ", error=" + failure.getMessage());
            failure.printStackTrace();
            throw failure;
        });
        builder.setBulkFlushMaxActions(ApplicationPropertiesConfig.readInt(
                "elasticsearch.bulk-flush-max-actions", "ES_BULK_FLUSH_MAX_ACTIONS", 200));
        builder.setBulkFlushInterval(1000);

        String auth = ElasticsearchAuthConfig.fromProperties().buildBasicAuth();
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        Header[] headers = new Header[]{new BasicHeader("Authorization", "Basic " + encodedAuth)};
        builder.setRestClientFactory(restClientBuilder -> {
            restClientBuilder.setDefaultHeaders(headers);
            restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                    .setConnectTimeout(30000)
                    .setSocketTimeout(60000));
        });

        return builder.build();
    }
}
