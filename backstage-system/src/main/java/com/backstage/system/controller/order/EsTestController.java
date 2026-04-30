package com.backstage.system.controller.order;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Api("ES 测试")
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
    @ApiOperation("测试 ES 是否连通")
    @Anonymous
    @GetMapping("/test")
    public R testEs() throws IOException {
        Request request = new Request("GET", "/");
        Response response = client.getLowLevelClient().performRequest(request);

        String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");

        Object json = JSON.parse(jsonStr);
        return R.ok(json);
    }

    /**
     * 查看集群健康状态
     * 访问：http://localhost:8080/es/health
     */
    @ApiOperation("查看集群健康状态")
    @Anonymous
    @GetMapping("/health")
    public R health() throws IOException {
        Request request = new Request("GET", "/_cat/health?v");
        Response response = client.getLowLevelClient().performRequest(request);

        String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");

        Object json = JSON.parse(jsonStr);
        return R.ok(json);
    }

    /**
     * 查看集群节点
     * 访问：http://localhost:8080/es/nodes
     */
    @ApiOperation("查看集群节点")
    @Anonymous
    @GetMapping("/nodes")
    public R nodes() throws IOException {
        Request request = new Request("GET", "/_cat/nodes?v");
        Response response = client.getLowLevelClient().performRequest(request);
        String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        return R.ok(jsonStr);
    }

    /**
     * 查看所有索引（表）
     * 访问：http://localhost:8080/es/indices
     */
    @ApiOperation("查看所有索引")
    @Anonymous
    @GetMapping("/indices")
    public R indices() throws IOException {
        Request request = new Request("GET", "/_cat/indices?v");
        Response response = client.getLowLevelClient().performRequest(request);
        String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        return R.ok(jsonStr);
    }

    /**
     * 查看集群详细状态
     * 访问：http://localhost:8080/es/cluster
     */
    @ApiOperation("查看集群详细状态")
    @Anonymous
    @GetMapping("/cluster")
    public R cluster() throws IOException {
        Request request = new Request("GET", "/_cluster/health?pretty");
        Response response = client.getLowLevelClient().performRequest(request);
        String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
        Object json = JSON.parse(jsonStr);
        return R.ok(json);
    }
}