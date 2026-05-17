package com.bachstage.tool.job_stream_from_kafka_to_es.config;

import com.bachstage.course.job_stream_from_kafka_to_es.config.ApplicationPropertiesConfig;

/**
 * 工具索引同步任务配置
 */
public class ToolIndexJobConfig
{
    private final String kafkaBootstrapServers;
    private final String kafkaGroupId;
    private final String kafkaStartMode;
    private final String topic;
    private final String esHosts;
    private final String esIndex;

    private ToolIndexJobConfig(String kafkaBootstrapServers, String kafkaGroupId, String kafkaStartMode, String topic,
                               String esHosts, String esIndex)
    {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.kafkaGroupId = kafkaGroupId;
        this.kafkaStartMode = kafkaStartMode;
        this.topic = topic;
        this.esHosts = esHosts;
        this.esIndex = esIndex;
    }

    public static ToolIndexJobConfig fromSystem()
    {
        return new ToolIndexJobConfig(
                ApplicationPropertiesConfig.read("kafka.bootstrap-servers", "KAFKA_BOOTSTRAP_SERVERS", "43.242.200.25:9092"),
                ApplicationPropertiesConfig.read("tool.index.group-id", "TOOL_INDEX_GROUP_ID", "backstage-tool-index-flink"),
                ApplicationPropertiesConfig.read("tool.index.start-mode", "TOOL_INDEX_START_MODE",
                        ApplicationPropertiesConfig.read("kafka.start-mode", "KAFKA_START_MODE", "earliest")),
                ApplicationPropertiesConfig.read("tool.index.topic", "TOOL_INDEX_TOPIC", "osh.tool.index"),
                ApplicationPropertiesConfig.read("elasticsearch.hosts", "ES_HOSTS", "http://43.242.200.25:9200"),
                ApplicationPropertiesConfig.read("tool.index.es-index", "TOOL_ES_INDEX", "osh_tool_search"));
    }

    public String getKafkaBootstrapServers()
    {
        return kafkaBootstrapServers;
    }

    public String getKafkaGroupId()
    {
        return kafkaGroupId;
    }

    public String getKafkaStartMode()
    {
        return kafkaStartMode;
    }

    public String getTopic()
    {
        return topic;
    }

    public String getEsHosts()
    {
        return esHosts;
    }

    public String getEsIndex()
    {
        return esIndex;
    }
}
