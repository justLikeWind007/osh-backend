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
    public static ElasticsearchSink<JSONObject> buildCreateSink(CourseIndexJobConfig config)
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
                        System.out.println("【ES写入】课程ID: " + message.getLong("id")
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
}
