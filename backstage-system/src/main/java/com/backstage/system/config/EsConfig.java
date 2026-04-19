package com.backstage.system.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class EsConfig {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    @Value("${elasticsearch.connect-timeout}")
    private long connectTimeout;

    @Value("${elasticsearch.socket-timeout}")
    private long socketTimeout;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo(host + ":" + port) // 动态拼接
                .withConnectTimeout(connectTimeout)
                .withSocketTimeout(socketTimeout)
                .build();
        return RestClients.create(config).rest();
    }
}