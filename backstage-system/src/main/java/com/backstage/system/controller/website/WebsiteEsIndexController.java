package com.backstage.system.controller.website;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Website ES 索引管理
 * 负责创建 website 相关的 ES 索引，只需执行一次
 */
@Api("Website ES 索引管理")
@RestController
@RequestMapping("/pc/website")
public class WebsiteEsIndexController {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 创建 website ES 索引
     * 只需调用一次，索引已存在时会提示，不会重复创建
     */
    @ApiOperation("创建 website ES 索引")
    @Anonymous
    @PostMapping("/es/createIndex")
    public R createWebsiteIndex() throws IOException {
        String indexName = "osh_practical_website";

        // 1. 先检查索引是否已存在
        Request checkRequest = new Request("HEAD", "/" + indexName);
        try {
            Response checkResponse = client.getLowLevelClient().performRequest(checkRequest);
            if (checkResponse.getStatusLine().getStatusCode() == 200) {
                return R.fail("索引已存在，无需重复创建");
            }
        } catch (Exception e) {
            // 返回 404 说明索引不存在，继续往下创建
        }

        // 2. 定义 Mapping，告诉 ES 每个字段的类型
        String mapping = "{\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"id\":              { \"type\": \"long\" },\n" +
                "      \"name\":            { \"type\": \"text\", \"analyzer\": \"ik_max_word\", \"search_analyzer\": \"ik_smart\" },\n" +
                "      \"url\":             { \"type\": \"keyword\" },\n" +
                "      \"description\":     { \"type\": \"text\", \"analyzer\": \"ik_max_word\", \"search_analyzer\": \"ik_smart\" },\n" +
                "      \"logoUrl\":         { \"type\": \"keyword\" },\n" +
                "      \"tags\":            { \"type\": \"keyword\" },\n" +
                "      \"clickCount\":      { \"type\": \"integer\" },\n" +
                "      \"goodCount\":       { \"type\": \"integer\" },\n" +
                "      \"midCount\":        { \"type\": \"integer\" },\n" +
                "      \"badCount\":        { \"type\": \"integer\" },\n" +
                "      \"collectionCount\": { \"type\": \"integer\" },\n" +
                "      \"ratingScore\":     { \"type\": \"double\" },\n" +
                "      \"auditTime\":       { \"type\": \"date\", \"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\" }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // 3. 发送 PUT 请求到 ES 创建索引
        Request request = new Request("PUT", "/" + indexName);
        request.setJsonEntity(mapping);
        Response response = client.getLowLevelClient().performRequest(request);

        // 4. 返回 ES 的响应结果
        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
        return R.ok(result);
    }
}
