package com.backstage.system.mapper.info_gap;

import com.backstage.common.utils.StringUtils;
import com.backstage.system.config.properties.SearchEsProperties;
import com.backstage.system.domain.dto.info_gap.InfoGapESSearchReqDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapSearchReqDTO;
import com.backstage.system.domain.info_gap.OshInfoGapEsDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.annotations.Mapper;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OshInfoGapEsMapper {

    private static final String INFO_GAP_SEARCH_INDEX = "osh_infogap_index";

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SearchEsProperties searchEsProperties;

    public InfoGapEsSearchResult searchInfoGaps(InfoGapESSearchReqDTO request) throws Exception {
        int pageNum = Optional.ofNullable(request.getPageNum()).orElse(1);
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(10);

        if (!restHighLevelClient.indices().exists(new GetIndexRequest(INFO_GAP_SEARCH_INDEX), RequestOptions.DEFAULT)) {
            return new InfoGapEsSearchResult(Collections.emptyList(), 0L, pageNum, pageSize);
        }

        SearchRequest searchRequest = new SearchRequest(INFO_GAP_SEARCH_INDEX);
        searchRequest.source(buildSearchSource(request, pageNum, pageSize));

        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() != null && ex.status().getStatus() == 404) {
                return new InfoGapEsSearchResult(Collections.emptyList(), 0L, pageNum, pageSize);
            }
            throw ex;
        }

        if (searchResponse.isTimedOut()) {
            throw new IllegalStateException("search info gaps from es timed out");
        }

        List<Long> ids = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            ids.add(Long.valueOf(searchHit.getId()));
        }

        return new InfoGapEsSearchResult(ids, searchResponse.getHits().getTotalHits().value, pageNum, pageSize);
    }

    public InfoGapEsSearchResult searchInfoGaps(InfoGapSearchReqDTO request) throws Exception {
        int pageNum = Optional.ofNullable(request.getPageNum()).orElse(1);
        int pageSize = Optional.ofNullable(request.getPageSize()).orElse(10);

        if (!restHighLevelClient.indices().exists(new GetIndexRequest(INFO_GAP_SEARCH_INDEX), RequestOptions.DEFAULT)) {
            return new InfoGapEsSearchResult(Collections.emptyList(), 0L, pageNum, pageSize);
        }

        SearchRequest searchRequest = new SearchRequest(INFO_GAP_SEARCH_INDEX);
        searchRequest.source(buildSearchSource(request, pageNum, pageSize));

        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() != null && ex.status().getStatus() == 404) {
                return new InfoGapEsSearchResult(Collections.emptyList(), 0L, pageNum, pageSize);
            }
            throw ex;
        }

        if (searchResponse.isTimedOut()) {
            throw new IllegalStateException("search info gaps from es timed out");
        }

        List<Long> ids = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            ids.add(Long.valueOf(searchHit.getId()));
        }

        return new InfoGapEsSearchResult(ids, searchResponse.getHits().getTotalHits().value, pageNum, pageSize);
    }

    public int bulkUpsertInfoGaps(List<OshInfoGapEsDocument> documents) throws Exception {
        if (documents == null || documents.isEmpty()) {
            return 0;
        }

        BulkRequest bulkRequest = new BulkRequest();
        for (OshInfoGapEsDocument document : documents) {
            bulkRequest.add(new IndexRequest(INFO_GAP_SEARCH_INDEX)
                    .id(String.valueOf(document.getId()))
                    .source(objectMapper.writeValueAsString(document), XContentType.JSON));
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (bulkResponse.hasFailures()) {
            throw new IllegalStateException("bulk upsert info gaps to es failed: " + bulkResponse.buildFailureMessage());
        }
        return documents.size();
    }

    public void upsertInfoGap(OshInfoGapEsDocument document) throws Exception {
        IndexRequest indexRequest = new IndexRequest(INFO_GAP_SEARCH_INDEX)
                .id(String.valueOf(document.getId()))
                .source(objectMapper.writeValueAsString(document), XContentType.JSON);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void deleteInfoGap(Long infoGapId) throws Exception {
        GetIndexRequest getIndexRequest = new GetIndexRequest(INFO_GAP_SEARCH_INDEX);
        if (!restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
            return;
        }
        DeleteRequest deleteRequest = new DeleteRequest(INFO_GAP_SEARCH_INDEX, String.valueOf(infoGapId));
        restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
    }

    public int deleteAllInfoGaps() throws Exception {
        GetIndexRequest getIndexRequest = new GetIndexRequest(INFO_GAP_SEARCH_INDEX);
        if (!restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
            return 0;
        }

        Request request = new Request("POST", "/" + INFO_GAP_SEARCH_INDEX + "/_delete_by_query");
        request.addParameter("refresh", "true");
        request.addParameter("conflicts", "proceed");
        request.setJsonEntity("{\"query\":{\"match_all\":{}}}");

        Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
        String json = EntityUtils.toString(response.getEntity(), "UTF-8");
        JsonNode root = objectMapper.readTree(json);
        return root.path("deleted").asInt(0);
    }

    private SearchSourceBuilder buildSearchSource(InfoGapESSearchReqDTO request, int pageNum, int pageSize) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from((pageNum - 1) * pageSize)
                .size(pageSize)
                .timeout(TimeValue.timeValueMillis(searchEsProperties.getFallbackTimeoutMillis()));

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("status", 4))
                .filter(QueryBuilders.termQuery("deleteFlag", 0));

        if (request != null && StringUtils.isNotEmpty(request.getKeyword())) {
            boolQuery.must(buildKeywordQuery(request.getKeyword()));
            sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }

        sourceBuilder.query(boolQuery);
        sourceBuilder.sort(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC).unmappedType("date"));
        return sourceBuilder;
    }

    private SearchSourceBuilder buildSearchSource(InfoGapSearchReqDTO request, int pageNum, int pageSize) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from((pageNum - 1) * pageSize)
                .size(pageSize)
                .timeout(TimeValue.timeValueMillis(searchEsProperties.getFallbackTimeoutMillis()));

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("status", 4))
                .filter(QueryBuilders.termQuery("deleteFlag", 0));

        if (request != null && StringUtils.isNotEmpty(request.getKeyword())) {
            boolQuery.must(buildKeywordQuery(request.getKeyword()));
            sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }

        if (request != null && request.getTagId() != null) {
            boolQuery.filter(QueryBuilders.termQuery("tagIds", request.getTagId()));
        }

        if (request != null && StringUtils.isNotEmpty(request.getCategory())) {
            boolQuery.filter(QueryBuilders.termQuery("category", request.getCategory()));
        }

        sourceBuilder.query(boolQuery);
        sourceBuilder.sort(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC).unmappedType("date"));
        return sourceBuilder;
    }

    private QueryBuilder buildKeywordQuery(String keyword) {
        return QueryBuilders.multiMatchQuery(keyword)
                .field("title", 8.0f)
                .field("tagNamesText", 4.0f)
                .field("category", 4.0f)
                .field("content", 3.0f)
                .field("searchText", 1.0f)
                .field("no", 8.0f)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
    }

    public static class InfoGapEsSearchResult {
        private final List<Long> ids;
        private final long total;
        private final int pageNum;
        private final int pageSize;

        public InfoGapEsSearchResult(List<Long> ids, long total, int pageNum, int pageSize) {
            this.ids = ids;
            this.total = total;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
        }

        public List<Long> getIds() {
            return ids;
        }

        public long getTotal() {
            return total;
        }

        public int getPageNum() {
            return pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }
    }
}
