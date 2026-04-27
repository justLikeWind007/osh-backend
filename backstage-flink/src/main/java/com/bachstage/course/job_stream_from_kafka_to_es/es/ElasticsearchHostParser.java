package com.bachstage.course.job_stream_from_kafka_to_es.es;

import org.apache.http.HttpHost;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Elasticsearch 地址解析器
 */
public class ElasticsearchHostParser
{
    public static List<HttpHost> parseHosts(String esHosts) throws MalformedURLException
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
