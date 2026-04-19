/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.backstage;

import com.alibaba.fastjson2.JSON;
import com.backstage.course.CourseIndexJobConfig;
import com.backstage.course.CourseIndexMessage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingJob
{
    private static final Logger log = LoggerFactory.getLogger(StreamingJob.class);

    public static void main(String[] args) throws Exception
    {
        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", config.getKafkaBootstrapServers());
        kafkaProps.setProperty("group.id", config.getKafkaGroupId());
        kafkaProps.setProperty("auto.offset.reset", config.getKafkaStartMode());

        FlinkKafkaConsumer<String> createConsumer =
                new FlinkKafkaConsumer<>(config.getCreateTopic(), new SimpleStringSchema(), kafkaProps);
        applyStartMode(createConsumer, config.getKafkaStartMode());

        DataStream<CourseIndexMessage> createStream = env
                .addSource(createConsumer)
                .name("course-index-create-source")
                .map((MapFunction<String, CourseIndexMessage>) value ->
                {
                    CourseIndexMessage message = JSON.parseObject(value, CourseIndexMessage.class);
                    log.info("Consumed create message, id={}, title={}", message.getId(), message.getTitle());
                    return message;
                })
                .name("course-index-create-parse");

        FlinkKafkaConsumer<String> updateConsumer =
                new FlinkKafkaConsumer<>(config.getUpdateTopic(), new SimpleStringSchema(), kafkaProps);
        applyStartMode(updateConsumer, config.getKafkaStartMode());

        DataStream<CourseIndexMessage> updateStream = env
                .addSource(updateConsumer)
                .name("course-index-update-source")
                .map((MapFunction<String, CourseIndexMessage>) value ->
                {
                    CourseIndexMessage message = JSON.parseObject(value, CourseIndexMessage.class);
                    log.info("Consumed update message, id={}, title={}", message.getId(), message.getTitle());
                    return message;
                })
                .name("course-index-update-parse");

        createStream.addSink(buildCreateSink(config)).name("course-index-create-es-sink");
        updateStream.addSink(buildUpdateSink(config)).name("course-index-update-es-sink");

        env.execute("backstage-course-index-job");
    }

    private static void applyStartMode(FlinkKafkaConsumer<String> consumer, String startMode)
    {
        String mode = startMode == null ? "" : startMode.trim().toLowerCase();
        switch (mode)
        {
            case "earliest":
                consumer.setStartFromEarliest();
                break;
            case "latest":
                consumer.setStartFromLatest();
                break;
            default:
                consumer.setStartFromGroupOffsets();
                break;
        }
    }

    private static ElasticsearchSink<CourseIndexMessage> buildCreateSink(CourseIndexJobConfig config)
            throws MalformedURLException
    {
        String indexName = config.getEsIndex();
        ElasticsearchSink.Builder<CourseIndexMessage> builder = new ElasticsearchSink.Builder<>(
                parseHosts(config.getEsHosts()),
                buildCreateSinkFunction(indexName));
        builder.setBulkFlushMaxActions(200);
        return builder.build();
    }

    private static ElasticsearchSink<CourseIndexMessage> buildUpdateSink(CourseIndexJobConfig config)
            throws MalformedURLException
    {
        String indexName = config.getEsIndex();
        ElasticsearchSink.Builder<CourseIndexMessage> builder = new ElasticsearchSink.Builder<>(
                parseHosts(config.getEsHosts()),
                buildUpdateSinkFunction(indexName));
        builder.setBulkFlushMaxActions(200);
        return builder.build();
    }

    static ElasticsearchSinkFunction<CourseIndexMessage> buildCreateSinkFunction(String indexName)
    {
        return (message, context, indexer) -> {
            IndexRequest request = Requests.indexRequest()
                    .index(indexName)
                    .type("_doc")
                    .id(String.valueOf(message.getId()))
                    .source(JSON.toJSONString(message), XContentType.JSON);
            indexer.add(request);
        };
    }

    static ElasticsearchSinkFunction<CourseIndexMessage> buildUpdateSinkFunction(String indexName)
    {
        return (message, context, indexer) -> {
            UpdateRequest request = new UpdateRequest(indexName, "_doc",
                    String.valueOf(message.getId()))
                    .doc(JSON.toJSONString(message), XContentType.JSON)
                    .docAsUpsert(true);
            indexer.add(request);
        };
    }

    private static List<HttpHost> parseHosts(String esHosts) throws MalformedURLException
    {
        List<HttpHost> hosts = new ArrayList<>();
        for (String rawHost : esHosts.split(","))
        {
            String value = rawHost.trim();
            if (value.isEmpty())
            {
                continue;
            }
            URL url = new URL(value);
            int port = url.getPort() > 0 ? url.getPort() : url.getDefaultPort();
            hosts.add(new HttpHost(url.getHost(), port, url.getProtocol()));
        }
        return hosts;
    }
}
