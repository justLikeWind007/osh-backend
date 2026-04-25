package com.bachstage.course.job_stream_from_kafka_to_es.config;

/**
 * Elasticsearch 认证配置
 */
public class ElasticsearchAuthConfig
{
    private final String username;
    private final String password;

    private ElasticsearchAuthConfig(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public static ElasticsearchAuthConfig fromProperties()
    {
        return new ElasticsearchAuthConfig(
                ApplicationPropertiesConfig.read("elasticsearch.username", "ES_USERNAME", "elastic"),
                ApplicationPropertiesConfig.read("elasticsearch.password", "ES_PASSWORD", "osh88888888"));
    }

    public String buildBasicAuth()
    {
        return username + ":" + password;
    }
}
