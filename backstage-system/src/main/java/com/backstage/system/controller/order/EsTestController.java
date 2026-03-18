package com.backstage.system.controller.order;

import com.backstage.common.annotation.Anonymous;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@RestController
@RequestMapping("/es")
public class EsTestController {

    @Autowired
    private RestHighLevelClient client;

    // ====================== 最常用 ES 命令 ======================

    /**
     * 测试 ES 是否连通
     * 访问：http://localhost:8080/es/test
     */
    @Anonymous
    @GetMapping("/test")
    public String testEs() throws IOException {
        Request request = new Request("GET", "/");
        Response response = client.getLowLevelClient().performRequest(request);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 查看集群健康状态
     * 访问：http://localhost:8080/es/health
     */
    @Anonymous
    @GetMapping("/health")
    public String health() throws IOException {
        Request request = new Request("GET", "/_cat/health?v");
        Response response = client.getLowLevelClient().performRequest(request);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 查看集群节点
     * 访问：http://localhost:8080/es/nodes
     */
    @Anonymous
    @GetMapping("/nodes")
    public String nodes() throws IOException {
        Request request = new Request("GET", "/_cat/nodes?v");
        Response response = client.getLowLevelClient().performRequest(request);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 查看所有索引（表）
     * 访问：http://localhost:8080/es/indices
     */
    @Anonymous
    @GetMapping("/indices")
    public String indices() throws IOException {
        Request request = new Request("GET", "/_cat/indices?v");
        Response response = client.getLowLevelClient().performRequest(request);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * 查看集群详细状态
     * 访问：http://localhost:8080/es/cluster
     */
    @Anonymous
    @GetMapping("/cluster")
    public String cluster() throws IOException {
        Request request = new Request("GET", "/_cluster/health?pretty");
        Response response = client.getLowLevelClient().performRequest(request);
        return EntityUtils.toString(response.getEntity());
    }
}