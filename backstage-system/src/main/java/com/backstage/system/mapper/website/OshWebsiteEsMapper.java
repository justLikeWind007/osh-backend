package com.backstage.system.mapper.website;

import com.backstage.common.response.PageResponse;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.config.properties.SearchEsProperties;
import com.backstage.system.domain.dto.website.WebsiteQueryDTO;
import com.backstage.system.domain.vo.website.OshPracticalWebsiteVO;
import com.backstage.system.domain.website.WebsiteEsDoc;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 实用网站 ES Mapper
 * 对标 OshCourseEsMapper，提供完整的 ES 操作能力
 * 
 * 功能：
 * 1. 搜索网站（支持关键词、标签过滤、分页）
 * 2. 批量写入网站文档
 * 3. 删除所有网站文档
 * 4. 重建索引
 */
@Component
public class OshWebsiteEsMapper {

    private static final String WEBSITE_SEARCH_INDEX = "osh_practical_website";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SearchEsProperties searchEsProperties;

    /**
     * 搜索网站
     * 
     * @param request 查询条件（关键词、标签、分页）
     * @return 分页结果
     * @throws Exception ES 异常
     */
    public PageResponse<OshPracticalWebsiteVO> searchWebsites(WebsiteQueryDTO request) throws Exception {
        int pageNum = request.getPageNum() == null ? 1 : request.getPageNum();
        int pageSize = request.getPageSize() == null ? 10 : request.getPageSize();

        // 索引不存在，返回空结果
        if (!restHighLevelClient.indices().exists(
                new GetIndexRequest(WEBSITE_SEARCH_INDEX), RequestOptions.DEFAULT)) {
            return PageResponse.of(new ArrayList<>(), 0L, pageNum, pageSize);
        }

        SearchRequest searchRequest = new SearchRequest(WEBSITE_SEARCH_INDEX);
        searchRequest.source(buildSearchSource(request, pageNum, pageSize));
        
        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() != null && ex.status().getStatus() == 404) {
                return PageResponse.of(new ArrayList<>(), 0L, pageNum, pageSize);
            }
            throw ex;
        }

        if (searchResponse.isTimedOut()) {
            throw new IllegalStateException("search websites from es timed out");
        }

        List<OshPracticalWebsiteVO> rows = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            WebsiteEsDoc document = objectMapper.convertValue(
                searchHit.getSourceAsMap(), 
                WebsiteEsDoc.class
            );
            rows.add(toVo(document));
        }

        return PageResponse.of(rows, searchResponse.getHits().getTotalHits().value, pageNum, pageSize);
    }

    /**
     * 批量写入/更新网站文档
     * 
     * @param documents 网站文档列表
     * @return 成功写入的文档数量
     * @throws Exception ES 异常
     */
    public int bulkUpsertWebsites(List<WebsiteEsDoc> documents) throws Exception {
        if (StringUtils.isEmpty(documents)) {
            return 0;
        }

        BulkRequest bulkRequest = new BulkRequest();
        for (WebsiteEsDoc document : documents) {
            bulkRequest.add(new IndexRequest(WEBSITE_SEARCH_INDEX)
                    .id(String.valueOf(document.getId()))
                    .source(objectMapper.writeValueAsString(document), XContentType.JSON));
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (bulkResponse.hasFailures()) {
            throw new IllegalStateException("bulk upsert websites to es failed: " 
                + bulkResponse.buildFailureMessage());
        }
        return documents.size();
    }

    /**
     * 删除所有网站文档（保留索引结构）
     * 
     * @return 删除的文档数量
     * @throws Exception ES 异常
     */
    public int deleteAllWebsites() throws Exception {
        GetIndexRequest getIndexRequest = new GetIndexRequest(WEBSITE_SEARCH_INDEX);
        if (!restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
            return 0;
        }

        Request request = new Request("POST", "/" + WEBSITE_SEARCH_INDEX + "/_delete_by_query");
        request.addParameter("refresh", "true");
        request.addParameter("conflicts", "proceed");
        request.setJsonEntity("{\"query\":{\"match_all\":{}}}");

        Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
        String json = EntityUtils.toString(response.getEntity(), "UTF-8");
        JsonNode root = objectMapper.readTree(json);
        return root.path("deleted").asInt(0);
    }

    /**
     * 重建索引（删除旧索引 + 创建新索引）
     * 
     * @param indexDefinitionJson 索引定义 JSON（settings + mappings）
     * @throws Exception ES 异常
     */
    public void recreateWebsiteSearchIndex(String indexDefinitionJson) throws Exception {
        GetIndexRequest getIndexRequest = new GetIndexRequest(WEBSITE_SEARCH_INDEX);
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        
        if (exists) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(WEBSITE_SEARCH_INDEX);
            restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        }

        CreateIndexRequest createIndexRequest = new CreateIndexRequest(WEBSITE_SEARCH_INDEX);
        createIndexRequest.source(indexDefinitionJson, XContentType.JSON);
        restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 构建搜索条件
     */
    private SearchSourceBuilder buildSearchSource(WebsiteQueryDTO request, int pageNum, int pageSize) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from((pageNum - 1) * pageSize)
                .size(pageSize)
                .timeout(TimeValue.timeValueMillis(searchEsProperties.getFallbackTimeoutMillis()));

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 关键词搜索：在 name 和 description 字段中搜索
        if (request != null && StringUtils.isNotEmpty(request.getWebsiteName())) {
            boolQuery.must(buildKeywordQuery(request.getWebsiteName()));
            // 有关键词时按相关性排序
            sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }

        // 标签过滤：精确匹配（tags 是 keyword 数组）
        if (request != null && request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("tags", request.getTagNames()));
        }

        sourceBuilder.query(boolQuery);
        // 兜底排序：按评分降序
        sourceBuilder.sort(SortBuilders.fieldSort("ratingScore").order(SortOrder.DESC));
        return sourceBuilder;
    }

    /**
     * 构建关键词查询（多字段搜索，带权重）
     */
    private QueryBuilder buildKeywordQuery(String keyword) {
        return QueryBuilders.multiMatchQuery(keyword)
                .field("name", 5.0f)         // 名称权重最高
                .field("description", 2.0f)  // 描述次之
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
    }

    /**
     * ES 文档转 VO
     */
    private OshPracticalWebsiteVO toVo(WebsiteEsDoc document) {
        OshPracticalWebsiteVO vo = new OshPracticalWebsiteVO();
        vo.setId(document.getId());
        vo.setName(document.getName());
        vo.setUrl(document.getUrl());
        vo.setDescription(document.getDescription());
        vo.setLogoUrl(document.getLogoUrl());
        vo.setClickCount(document.getClickCount());
        vo.setGoodCount(document.getGoodCount());
        vo.setMidCount(document.getMidCount());
        vo.setBadCount(document.getBadCount());
        vo.setCollectionCount(document.getCollectionCount());
        vo.setRatingScore(document.getRatingScore());
        vo.setAuditTime(document.getAuditTime());
        
        // tags 从 List<String> 转成逗号分隔字符串
        if (document.getTags() != null && !document.getTags().isEmpty()) {
            vo.setTags(String.join(",", document.getTags()));
        }
        
        return vo;
    }
}
