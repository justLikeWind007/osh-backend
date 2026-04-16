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

public class StreamingJob
{
    public static void main(String[] args) throws Exception
    {
        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", config.getKafkaBootstrapServers());
        kafkaProps.setProperty("group.id", config.getKafkaGroupId());
        kafkaProps.setProperty("auto.offset.reset", "latest");

        DataStream<CourseIndexMessage> createStream = env
                .addSource(new FlinkKafkaConsumer<>(config.getCreateTopic(), new SimpleStringSchema(), kafkaProps))
                .name("course-index-create-source")
                .map((MapFunction<String, CourseIndexMessage>) value -> JSON.parseObject(value, CourseIndexMessage.class))
                .name("course-index-create-parse");

        DataStream<CourseIndexMessage> updateStream = env
                .addSource(new FlinkKafkaConsumer<>(config.getUpdateTopic(), new SimpleStringSchema(), kafkaProps))
                .name("course-index-update-source")
                .map((MapFunction<String, CourseIndexMessage>) value -> JSON.parseObject(value, CourseIndexMessage.class))
                .name("course-index-update-parse");

        createStream.addSink(buildCreateSink(config)).name("course-index-create-es-sink");
        updateStream.addSink(buildUpdateSink(config)).name("course-index-update-es-sink");

        env.execute("backstage-course-index-job");
    }

    private static ElasticsearchSink<CourseIndexMessage> buildCreateSink(CourseIndexJobConfig config)
            throws MalformedURLException
    {
        ElasticsearchSink.Builder<CourseIndexMessage> builder = new ElasticsearchSink.Builder<>(
                parseHosts(config.getEsHosts()),
                (ElasticsearchSinkFunction<CourseIndexMessage>) (message, context, indexer) -> {
                    IndexRequest request = Requests.indexRequest()
                            .index(config.getEsIndex())
                            .type("_doc")
                            .id(String.valueOf(message.getCourseId()))
                            .source(JSON.toJSONString(message), XContentType.JSON);
                    indexer.add(request);
                });
        builder.setBulkFlushMaxActions(200);
        return builder.build();
    }

    private static ElasticsearchSink<CourseIndexMessage> buildUpdateSink(CourseIndexJobConfig config)
            throws MalformedURLException
    {
        ElasticsearchSink.Builder<CourseIndexMessage> builder = new ElasticsearchSink.Builder<>(
                parseHosts(config.getEsHosts()),
                (ElasticsearchSinkFunction<CourseIndexMessage>) (message, context, indexer) -> {
                    UpdateRequest request = new UpdateRequest(config.getEsIndex(), "_doc",
                            String.valueOf(message.getCourseId()))
                            .doc(JSON.toJSONString(message), XContentType.JSON)
                            .docAsUpsert(true);
                    indexer.add(request);
                });
        builder.setBulkFlushMaxActions(200);
        return builder.build();
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
