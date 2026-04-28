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

package com.backstage.test_from_kafka_to_es;

import com.alibaba.fastjson2.JSON;
import com.backstage.test_from_kafka_to_es.course.CourseIndexJobConfig;
import com.backstage.test_from_kafka_to_es.course.CourseIndexMessage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * Flink 课程索引任务
 * 
 * 功能：从 Kafka 消费课程数据，过滤后写入 Elasticsearch
 * 
 * 运行方式：
 * 
 * 1. Maven 运行：
 *    mvn exec:java -Dexec.mainClass="com.backstage.test_from_kafka_to_es.StreamingJob"
 * 
 * 2. IDEA 运行（Debug 模式）：
 *    右键 StreamingJob.java → Debug 'StreamingJob.main()'
 *    设置断点后可以单步调试
 * 
 * 数据流程：
 *    Kafka → Flink 消费 → 过滤(status=1) → Elasticsearch
 * 
 * 配置：
 *    - Kafka: 43.242.200.25:9092
 *    - ES: http://43.242.200.25:9200 (elastic/osh88888888)
 *    - Topic: osh.course.index.create / osh.course.index.update
 * 
 * 注意：
 *    - 使用 earliest 从头开始消费，确保能读取到测试数据
 *    - 如果要从最新位置消费，改为 latest
 */
public class StreamingJob
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("========================================");
        System.out.println("Flink StreamingJob 启动中...");
        System.out.println("========================================");
        
        CourseIndexJobConfig config = CourseIndexJobConfig.fromSystem();
        
        // 打印配置信息
        System.out.println("配置信息:");
        System.out.println("  Kafka: " + config.getKafkaBootstrapServers());
        System.out.println("  消费者组: " + config.getKafkaGroupId());
        System.out.println("  创建 Topic: " + config.getCreateTopic());
        System.out.println("  更新 Topic: " + config.getUpdateTopic());
        System.out.println("  ES: " + config.getEsHosts());
        System.out.println("  ES 索引: " + config.getEsIndex());
        System.out.println("========================================\n");
        
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", config.getKafkaBootstrapServers());
        kafkaProps.setProperty("group.id", config.getKafkaGroupId());
        // 修改为 earliest，从头开始消费，确保能读取到测试数据
        kafkaProps.setProperty("auto.offset.reset", "earliest");
        // 添加更多 Kafka 配置，提高稳定性
        kafkaProps.setProperty("enable.auto.commit", "true");
        kafkaProps.setProperty("auto.commit.interval.ms", "1000");
        kafkaProps.setProperty("session.timeout.ms", "30000");
        kafkaProps.setProperty("request.timeout.ms", "40000");

        System.out.println("正在连接 Kafka 和 ES...");
        
        // 创建流：从 Kafka 读取 -> 解析 JSON -> 过滤（只保留 status=2 的课程）-> 写入 ES
        DataStream<CourseIndexMessage> createStream = env
                .addSource(new FlinkKafkaConsumer<>(config.getCreateTopic(), new SimpleStringSchema(), kafkaProps))
                .name("course-index-create-source")
                .map((MapFunction<String, CourseIndexMessage>) value -> {
                    System.out.println("【创建流】收到消息: " + value);
                    return JSON.parseObject(value, CourseIndexMessage.class);
                })
                .name("course-index-create-parse")
                .filter(message -> {
                    // 过滤逻辑：只保留状态为 2（已发布）的课程
                    boolean pass = message.getStatus() != null && message.getStatus() == 2;
                    System.out.println("【创建流】过滤检查 - 课程ID: " + message.getCourseId() + 
                        ", 状态: " + message.getStatus() + ", 是否通过: " + pass);
                    return pass;
                })
                .name("course-index-create-filter");

        // 更新流：从 Kafka 读取 -> 解析 JSON -> 过滤（只保留 status=2 的课程）-> 写入 ES
        DataStream<CourseIndexMessage> updateStream = env
                .addSource(new FlinkKafkaConsumer<>(config.getUpdateTopic(), new SimpleStringSchema(), kafkaProps))
                .name("course-index-update-source")
                .map((MapFunction<String, CourseIndexMessage>) value -> {
                    System.out.println("【更新流】收到消息: " + value);
                    return JSON.parseObject(value, CourseIndexMessage.class);
                })
                .name("course-index-update-parse")
                .filter(message -> {
                    // 过滤逻辑：只保留状态为 2（已发布）的课程
                    boolean pass = message.getStatus() != null && message.getStatus() == 2;
                    System.out.println("【更新流】过滤检查 - 课程ID: " + message.getCourseId() + 
                        ", 状态: " + message.getStatus() + ", 是否通过: " + pass);
                    return pass;
                })
                .name("course-index-update-filter");

        createStream.addSink(buildCreateSink(config)).name("course-index-create-es-sink");
        updateStream.addSink(buildUpdateSink(config)).name("course-index-update-es-sink");

        System.out.println("Flink 任务已启动，等待消息...\n");
        env.execute("backstage-course-index-job");
    }

    private static ElasticsearchSink<CourseIndexMessage> buildCreateSink(CourseIndexJobConfig config)
            throws MalformedURLException
    {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = parseHosts(config.getEsHosts());
        
        ElasticsearchSink.Builder<CourseIndexMessage> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<CourseIndexMessage>() {
                    @Override
                    public void process(CourseIndexMessage message, RuntimeContext context, RequestIndexer indexer) {
                        // 手动构建 JSON，将 Date 转换为时间戳（毫秒），避免 ES 日期格式解析错误
                        String jsonString = buildJsonWithTimestamp(message);
                        IndexRequest request = Requests.indexRequest()
                                .index(esIndex)
                                .type("_doc")
                                .id(String.valueOf(message.getCourseId()))
                                .source(jsonString, XContentType.JSON);
                        System.out.println("【ES写入】课程ID: " + message.getCourseId() + ", 标题: " + message.getTitle());
                        indexer.add(request);
                    }
                    
                    private String buildJsonWithTimestamp(CourseIndexMessage message) {
                        // 先转为 JSONObject，然后替换 Date 字段为时间戳
                        com.alibaba.fastjson2.JSONObject json = (com.alibaba.fastjson2.JSONObject) JSON.toJSON(message);
                        if (message.getCreateTime() != null) {
                            json.put("createTime", message.getCreateTime().getTime());
                        }
                        if (message.getUpdateTime() != null) {
                            json.put("updateTime", message.getUpdateTime().getTime());
                        }
                        return json.toJSONString();
                    }
                });
        
        builder.setBulkFlushMaxActions(1);  // 每条数据立即 flush，方便调试
        
        // 添加 ES 基本认证和超时配置
        String auth = "elastic:osh88888888";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        Header[] headers = new Header[]{new BasicHeader("Authorization", "Basic " + encodedAuth)};
        builder.setRestClientFactory(restClientBuilder -> {
            restClientBuilder.setDefaultHeaders(headers);
            // 增加连接超时时间
            restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
                return requestConfigBuilder
                    .setConnectTimeout(30000)  // 连接超时 30 秒
                    .setSocketTimeout(60000);   // 读取超时 60 秒
            });
        });
        
        return builder.build();
    }

    private static ElasticsearchSink<CourseIndexMessage> buildUpdateSink(CourseIndexJobConfig config)
            throws MalformedURLException
    {
        final String esIndex = config.getEsIndex();
        List<HttpHost> httpHosts = parseHosts(config.getEsHosts());
        
        ElasticsearchSink.Builder<CourseIndexMessage> builder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<CourseIndexMessage>() {
                    @Override
                    public void process(CourseIndexMessage message, RuntimeContext context, RequestIndexer indexer) {
                        // 手动构建 JSON，将 Date 转换为时间戳（毫秒），避免 ES 日期格式解析错误
                        String jsonString = buildJsonWithTimestamp(message);
                        UpdateRequest request = new UpdateRequest(esIndex, "_doc",
                                String.valueOf(message.getCourseId()))
                                .doc(jsonString, XContentType.JSON)
                                .docAsUpsert(true);
                        System.out.println("【ES更新】课程ID: " + message.getCourseId() + ", 标题: " + message.getTitle());
                        indexer.add(request);
                    }
                    
                    private String buildJsonWithTimestamp(CourseIndexMessage message) {
                        // 先转为 JSONObject，然后替换 Date 字段为时间戳
                        com.alibaba.fastjson2.JSONObject json = (com.alibaba.fastjson2.JSONObject) JSON.toJSON(message);
                        if (message.getCreateTime() != null) {
                            json.put("createTime", message.getCreateTime().getTime());
                        }
                        if (message.getUpdateTime() != null) {
                            json.put("updateTime", message.getUpdateTime().getTime());
                        }
                        return json.toJSONString();
                    }
                });
        
        builder.setBulkFlushMaxActions(1);  // 每条数据立即 flush，方便调试
        
        // 添加 ES 基本认证和超时配置
        String auth = "elastic:osh88888888";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        Header[] headers = new Header[]{new BasicHeader("Authorization", "Basic " + encodedAuth)};
        builder.setRestClientFactory(restClientBuilder -> {
            restClientBuilder.setDefaultHeaders(headers);
            // 增加连接超时时间
            restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
                return requestConfigBuilder
                    .setConnectTimeout(30000)  // 连接超时 30 秒
                    .setSocketTimeout(60000);   // 读取超时 60 秒
            });
        });
        
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
